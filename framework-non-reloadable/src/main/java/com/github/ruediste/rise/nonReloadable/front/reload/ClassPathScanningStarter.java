package com.github.ruediste.rise.nonReloadable.front.reload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.jar.JarFile;

import javax.inject.Inject;
import javax.inject.Named;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.front.StartupTimeLogger;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathWalker.ClassPathVisitResult;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathWalker.ClassPathVisitor;
import com.github.ruediste.rise.util.Pair;
import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;

/**
 * Initializer to setup classpath scanning and reloading
 */
public class ClassPathScanningStarter {

    @Inject
    Logger log;

    @Inject
    CoreConfigurationNonRestartable config;

    @Inject
    ClassChangeNotifier classChangeNotifier;

    @Inject
    @Named("classPath")
    FileChangeNotifier fileChangeNotifier;

    public void start() {
        log.debug("Start Classpath scanning ...");
        Stopwatch watch = Stopwatch.createStarted();
        ConcurrentHashMap<String, Pair<ClassNode, List<String>>> classes = new ConcurrentHashMap<>();
        Set<Path> rootDirs = Collections
                .newSetFromMap(new ConcurrentHashMap<>());

        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 10, 10,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),
                new CallerRunsPolicy());

        ClassPathWalker.scan(Thread.currentThread().getContextClassLoader(),
                new ClassPathVisitor() {

                    @Override
                    public ClassPathVisitResult visitRootDirectory(
                            Path rootDirectory, ClassLoader classloader) {
                        rootDirs.add(rootDirectory);
                        return ClassPathVisitResult.SKIP_CONTENTS;
                    }

                    @Override
                    public void visitResource(String name,
                            ClassLoader classLoader,
                            Supplier<InputStream> inputStreamSupplier) {
                        // nop
                    }

                    @Override
                    public ClassPathVisitResult visitJarFile(Path path,
                            JarFile jarFile, ClassLoader classloader) {
                        return ClassPathVisitResult.CONTINUE;
                    }

                    @Override
                    public void visitClass(String className,
                            ClassLoader classLoader,
                            Supplier<InputStream> inputStreamSupplier) {
                        if (!config.shouldBeScanned(className)) {
                            log.trace("not scanning {}", className);
                            return;
                        }
                        String internalClassName = className.replace('.', '/');
                        log.trace("scanning class {}", internalClassName);

                        if (classes.containsKey(internalClassName))
                            return;

                        byte[] bb = null;
                        try (InputStream in = inputStreamSupplier.get()) {
                            bb = ByteStreams.toByteArray(in);
                        } catch (IOException e) {
                            log.error("Unable to read class " + className, e);
                        }
                        if (bb != null) {
                            final byte[] b = bb;
                            executor.execute(() -> {
                                if (classes.containsKey(internalClassName))
                                    return;
                                classes.putIfAbsent(internalClassName,
                                        readClass(b));
                            });
                        }

                    }
                });

        classChangeNotifier.initialize(trx -> {
            if (trx.isInitial) {
                classes.entrySet().forEach(e -> {
                    trx.addedClasses.add(e.getValue().getA());
                    trx.addedClassesMembers.put(e.getKey(),
                            e.getValue().getB());
                });
            }
        });

        StartupTimeLogger.stopAndLog("Classpath Scanning", watch);
        log.debug("Classpath scanning done");

        // start notifier
        fileChangeNotifier.start(rootDirs, config.fileChangeSettleDelayMs);
    }

    protected Pair<ClassNode, List<String>> readClass(byte[] bb) {
        return readClass(() -> new ByteArrayInputStream(bb));
    }

    protected Pair<ClassNode, List<String>> readClass(
            Supplier<InputStream> inputStreamSupplier) {
        ClassNode node = new ClassNode();
        MemberOrderVisitor orderVisitor = new MemberOrderVisitor(node);
        try (InputStream in = inputStreamSupplier.get()) {
            new ClassReader(in).accept(orderVisitor, config.classScanningFlags);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Pair.of(node, orderVisitor.getMembers());
    }
}
