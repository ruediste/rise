package com.github.ruediste.rise.nonReloadable.front.reload;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.front.ApplicationEventQueue;
import com.github.ruediste.rise.nonReloadable.front.reload.ResourceChangeNotifier.ResourceChangeTransaction;
import com.github.ruediste.rise.util.AsmUtil;
import com.github.ruediste.rise.util.Pair;

/**
 * Notify listeners of changes to classes. Changes of multiple classes are
 * bundled into a transaction. The nogifications are based on the
 * {@link ResourceChangeNotifier}
 */
@Singleton
public class ClassChangeNotifier {

    @Inject
    Logger log;

    @Inject
    CoreConfigurationNonRestartable config;

    @Inject
    ResourceChangeNotifier notifier;

    @Inject
    ApplicationEventQueue queue;

    private Map<String, String> classNameMap = new HashMap<>();

    public static class ClassChangeTransaction {
        public Set<String> removedClasses = new HashSet<>();
        public Set<ClassNode> addedClasses = new HashSet<>();
        public Set<ClassNode> modifiedClasses = new HashSet<>();
        public Map<String, List<String>> addedClassesMembers = new HashMap<>();
        public Map<String, List<String>> modifiedClassesMembers = new HashMap<>();
        public boolean isInitial;
    }

    private LinkedHashSet<Consumer<ClassChangeTransaction>> preListeners = new LinkedHashSet<>();
    private LinkedHashSet<Consumer<ClassChangeTransaction>> listeners = new LinkedHashSet<>();

    /**
     * Add a listener which will be notified before those registered with
     * {@link #addListener(Consumer)}
     */
    public void addPreListener(Consumer<ClassChangeTransaction> listener) {
        notifier.checkNotStarted();
        preListeners.add(listener);
    }

    /**
     * Add a listener. May only be called before the underlying
     * {@link FileChangeNotifier} has been started.
     */
    public void addListener(Consumer<ClassChangeTransaction> listener) {
        notifier.checkNotStarted();
        listeners.add(listener);
    }

    public void removeListener(Consumer<ClassChangeTransaction> listener) {
        listeners.remove(listener);
    }

    @PostConstruct
    void setup() {
        notifier.addListener(this::changeOccurred);
    }

    void changeOccurred(ResourceChangeTransaction trx) {
        ClassChangeTransaction classTrx = new ClassChangeTransaction();
        classTrx.isInitial = trx.isInitial;

        for (String file : trx.removedResources) {
            if (!file.endsWith(".class")) {
                continue;
            }

            String name = classNameMap.get(file);
            if (name != null) {
                classNameMap.remove(file);
                classTrx.removedClasses.add(name);
            }
        }

        trx.addedResources.entrySet().stream().parallel().filter(pair -> pair.getKey().endsWith(".class"))
                .map(entry -> Pair.of(entry.getKey(), readClass(entry.getValue()))).sequential().forEach(pair -> {
                    String name = pair.getB().getA().name;
                    classNameMap.put(pair.getA(), name);
                    classTrx.addedClasses.add(pair.getB().getA());
                    classTrx.addedClassesMembers.put(name, pair.getB().getB());
                });

        // scan some additional classes for the initial transaction
        if (trx.isInitial) {
            config.getAdditionalScannedClasses().stream().map(cls -> AsmUtil.readClassWithMembers(cls))
                    .forEach(pair -> {
                        classTrx.addedClasses.add(pair.getA());
                        classTrx.addedClassesMembers.put(pair.getA().name, pair.getB());
                    });
        }

        for (Entry<String, byte[]> entry : trx.modifiedResources.entrySet()) {
            if (!entry.getKey().endsWith(".class")) {
                continue;
            }
            Pair<ClassNode, List<String>> pair = readClass(entry.getValue());
            classTrx.modifiedClasses.add(pair.getA());
            classTrx.modifiedClassesMembers.put(pair.getA().name, pair.getB());
        }

        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Publishing Class Change Transaction: \n");
            sb.append("Added:\n");
            sb.append(classTrx.addedClasses.stream().map(n -> n.name).collect(joining("\n", "  ", "")));
            sb.append("Removed:\n");
            sb.append(classTrx.removedClasses.stream().collect(joining("\n", "  ", "")));
            sb.append("Modified:\n");
            sb.append(classTrx.modifiedClasses.stream().map(n -> n.name).collect(joining("\n", "  ", "")));
            log.trace(sb.toString());
        }
        for (Consumer<ClassChangeTransaction> listener : new ArrayList<>(preListeners)) {
            listener.accept(classTrx);
        }
        for (Consumer<ClassChangeTransaction> listener : new ArrayList<>(listeners)) {
            listener.accept(classTrx);
        }
    }

    Pair<ClassNode, List<String>> readClass(byte[] bs) {
        ClassNode node = new ClassNode();

        MemberOrderVisitor orderVisitor = new MemberOrderVisitor(node);
        new ClassReader(bs).accept(orderVisitor, config.classScanningFlags);
        return Pair.of(node, orderVisitor.getMembers());
    }
}
