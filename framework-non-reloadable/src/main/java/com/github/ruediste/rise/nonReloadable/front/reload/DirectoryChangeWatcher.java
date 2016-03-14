package com.github.ruediste.rise.nonReloadable.front.reload;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.front.ApplicationEventQueue;
import com.google.common.base.Preconditions;

/**
 * Watcher for changes to a set of directory trees.
 * 
 * <p>
 * The watcher keeps itself registered on newly created sub directories.
 * However, it is not possible to stay up to date on the contents of a directory
 * hierarchy with the watcher alone. Use the {@link FileChangeNotifier} for this
 * purpose.
 * </p>
 */
public class DirectoryChangeWatcher {
    @Inject
    private Logger log;

    @Inject
    private ApplicationEventQueue queue;

    private long settleDelayMs;
    private ScheduledFuture<?> task;
    private volatile boolean isRunning = true;
    private Consumer<Set<Path>> listener;
    private Map<Path, WatchKey> watchKeys = new HashMap<>();
    private Set<Path> rootDirs = new HashSet<>();
    private WatchService watchService;
    private Thread watchThread;

    /**
     * Start watching the filesystem
     *
     * @param rootDirs
     *            roots of the directory trees to be watched
     * @param listener
     *            Listener which will be notified with the affected paths. Not
     *            all affected paths will be reported. Use the
     *            {@link FileChangeNotifier} to stay up to date with all changes
     *            to a directory. The listener is called in the application
     *            event thread
     * @param settleDelayMs
     *            delay in milli seconds before changes are reported.
     */
    public void start(Collection<? extends Path> rootDirs, Consumer<Set<Path>> listener, long settleDelayMs) {
        queue.checkAET();

        Preconditions.checkNotNull(listener, "listener");
        Preconditions.checkArgument(settleDelayMs >= 0, "settleDealy needs to be positive: %s", settleDelayMs);
        this.listener = listener;
        this.settleDelayMs = settleDelayMs;
        this.rootDirs.addAll(rootDirs);
        try {
            watchService = FileSystems.getDefault().newWatchService();
            registerTrees();

            watchThread = new Thread(new WatchLoopRunnable(), "Watch");
            watchThread.start();
        } catch (IOException e) {
            throw new RuntimeException("Error during initialization", e);
        }

    }

    /**
     * Close the watcher. No notifications will be sent after this method
     * returns
     */
    public void close() {
        queue.checkAET();

        isRunning = false;
        watchThread.interrupt();
        if (task != null) {
            task.cancel(false);
        }
        try {
            watchThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void unregisterStaleWatchKeys() {
        Set<Path> stalePaths = new HashSet<Path>();

        for (Path path : watchKeys.keySet()) {
            if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
                stalePaths.add(path);
            }
        }

        if (stalePaths.size() > 0) {
            for (Path stalePath : stalePaths) {
                WatchKey watchKey = watchKeys.get(stalePath);
                watchKey.cancel();
                watchKeys.remove(watchKey);
            }
        }
    }

    private void registerDir(Path dir) {
        if (!watchKeys.containsKey(dir)) {
            log.debug("Registering " + dir);

            try {
                WatchKey watchKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.OVERFLOW);
                watchKeys.put(dir, watchKey);
            } catch (IOException e) {
                log.info("Error while registering watch key", e);
            }
        }
    }

    private void registerTrees() {
        for (Path root : rootDirs) {
            registerTree(root);
        }
    }

    private void registerTree(Path root) {
        try {
            Files.walkFileTree(root, new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    registerDir(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("error while registering directory tree " + root, e);
        }
    }

    Object affectedPathsLock = new Object();
    Set<Path> affectedPaths = new HashSet<>();

    private void timerElapsed() {
        registerTrees();
        unregisterStaleWatchKeys();
        Set<Path> paths;
        synchronized (affectedPathsLock) {
            paths = affectedPaths;
            affectedPaths = new HashSet<>();
        }
        listener.accept(paths);
    }

    private final class WatchLoopRunnable implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                WatchKey key;
                try {
                    // get next event, drop it
                    key = watchService.take();
                    Path dir = (Path) key.watchable();
                    ArrayList<Path> paths = new ArrayList<>();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path path = dir.resolve((Path) event.context());
                        paths.add(path);
                    }
                    synchronized (affectedPathsLock) {
                        affectedPaths.addAll(paths);
                    }
                    key.reset();

                    // reset timer task
                    if (task != null) {
                        task.cancel(false);
                    }
                    task = queue.schedule(() -> timerElapsed(), settleDelayMs, TimeUnit.MILLISECONDS);

                } catch (InterruptedException e) {
                    isRunning = false;
                }
            }
        }
    }
}
