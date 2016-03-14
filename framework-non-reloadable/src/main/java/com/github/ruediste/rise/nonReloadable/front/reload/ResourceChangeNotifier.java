package com.github.ruediste.rise.nonReloadable.front.reload;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.jar.JarFile;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.front.StartupTimeLogger;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathWalker.ClassPathVisitResult;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathWalker.ClassPathVisitor;
import com.github.ruediste.rise.nonReloadable.front.reload.FileChangeNotifier.FileChangeTransaction;
import com.github.ruediste.rise.util.Pair;
import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;

/**
 * Notifier for changes to the available classpath resources. Only notifications
 * for resources which pass
 * {@link CoreConfigurationNonRestartable#shouldClasspathResourceBeScanned(String)}
 * are generated.
 */
@Singleton
public class ResourceChangeNotifier {

    @Inject
    Logger log;

    @Inject
    CoreConfigurationNonRestartable config;

    @Inject
    FileChangeNotifier fileChangeNotifier;

    private LinkedHashSet<Consumer<ResourceChangeTransaction>> preListeners = new LinkedHashSet<>();
    private LinkedHashSet<Consumer<ResourceChangeTransaction>> listeners = new LinkedHashSet<>();
    private boolean isStarted;

    public void addPreListener(Consumer<ResourceChangeTransaction> listener) {
        preListeners.add(listener);
    }

    public void addListener(Consumer<ResourceChangeTransaction> listener) {
        listeners.add(listener);
    }

    /**
     * Throw an exception if this notifier is already started
     */
    public synchronized void checkNotStarted() {
        if (isStarted) {
            throw new IllegalStateException("Notifier is already started");
        }
    }

    public static class ResourceChangeTransaction {
        public Set<String> removedResources = new HashSet<>();
        public Map<String, byte[]> addedResources = new HashMap<>();
        public Map<String, byte[]> modifiedResources = new HashMap<>();
        public boolean isInitial;
    }

    @PostConstruct
    void postConstruct() {
        fileChangeNotifier.addListener(this::onChange);
    }

    private static Map<String, byte[]> jarResources;

    private void onChange(FileChangeTransaction trx) {
        ResourceChangeTransaction resourceTrx = new ResourceChangeTransaction();
        resourceTrx.isInitial = trx.isInitial;
        for (Pair<Path, Path> removedFile : trx.removedFiles) {
            String name = resourceName(removedFile);
            if (config.shouldClasspathResourceBeScanned(name))
                resourceTrx.removedResources.add(name);
        }

        processFiles(trx.addedFiles, resourceTrx.addedResources);
        processFiles(trx.modifiedFiles, resourceTrx.modifiedResources);
        if (trx.isInitial) {
            resourceTrx.addedResources.putAll(jarResources);
            jarResources = null;
        }

        preListeners.forEach(x -> x.accept(resourceTrx));
        listeners.forEach(x -> x.accept(resourceTrx));
    }

    private void processFiles(Set<Pair<Path, Path>> in, Map<String, byte[]> out) {
        for (Pair<Path, Path> addedFile : in) {
            try {
                String name = resourceName(addedFile);
                if (config.shouldClasspathResourceBeScanned(name)) {
                    out.put(name, Files.readAllBytes(addedFile.getB()));
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while reading " + addedFile.getB(), e);
            }
        }
    }

    private String resourceName(Pair<Path, Path> rootPathPair) {
        Path path = rootPathPair.getA().normalize().relativize(rootPathPair.getB().normalize());
        String resourceName = StreamSupport.stream(path.spliterator(), false).map(Path::toString).collect(joining("/"));
        return resourceName;
    }

    public void start() {
        synchronized (this) {
            isStarted = true;
        }
        log.debug("Start Classpath scanning ...");
        Stopwatch watch = Stopwatch.createStarted();
        jarResources = new ConcurrentHashMap<>();
        Set<Path> rootDirs = Collections.newSetFromMap(new ConcurrentHashMap<>());

        ClassPathWalker.scan(Thread.currentThread().getContextClassLoader(), new ClassPathVisitor() {

            @Override
            public ClassPathVisitResult visitRootDirectory(Path rootDirectory, ClassLoader classloader) {
                rootDirs.add(rootDirectory);
                // directories will be scanned by the FileChangeNotifier
                return ClassPathVisitResult.SKIP_CONTENTS;
            }

            @Override
            public void visitResource(String name, ClassLoader classLoader, Supplier<InputStream> inputStreamSupplier) {
                // only visited for resources in jar files
                if (config.shouldClasspathResourceBeScanned(name)) {
                    byte[] bb = null;
                    try (InputStream in = inputStreamSupplier.get()) {
                        bb = ByteStreams.toByteArray(in);
                    } catch (IOException e) {
                        log.error("Unable to read resource " + name, e);
                    }
                    jarResources.put(name, bb);
                }
            }

            @Override
            public ClassPathVisitResult visitJarFile(Path path, JarFile jarFile, ClassLoader classloader) {
                return ClassPathVisitResult.CONTINUE;
            }

            @Override
            public void visitClass(String resourceName, String className, ClassLoader classLoader,
                    Supplier<InputStream> inputStreamSupplier) {
                visitResource(resourceName, classLoader, inputStreamSupplier);
            }
        });

        // start notifier
        fileChangeNotifier.start(rootDirs, config.fileChangeSettleDelayMs);

        StartupTimeLogger.stopAndLog("Classpath Scanning", watch);
        log.debug("Classpath scanning done");
    }

    /**
     * Close the notifier. No notifications will be sent after this method
     * returns
     */
    public void close() {
        fileChangeNotifier.close();
    }
}
