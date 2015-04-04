package com.github.ruediste.laf.core.classReload;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

public class DirectoryChangeWatcher {
	@Inject
	private Logger log;
	private long settleDelayMs;
	private TimerTask task;
	private volatile boolean isRunning = true;
	private Timer timer;
	private Runnable listener;
	private Map<Path, WatchKey> watchKeys = new HashMap<>();
	private Set<Path> rootDirs = new HashSet<>();
	private WatchService watchService;
	private Thread watchThread;

	public void start(Collection<? extends Path> rootDirs, Runnable listener,
			long settleDelayMs) {
		Preconditions.checkNotNull(listener, "listener");
		Preconditions.checkArgument(settleDelayMs >= 0,
				"settleDealy needs to be positive: %s", settleDelayMs);
		this.listener = listener;
		this.settleDelayMs = settleDelayMs;
		this.rootDirs.addAll(rootDirs);
		try {

			timer = new Timer("Watch", true);

			watchService = FileSystems.getDefault().newWatchService();
			watchThread = new Thread(new WatchLoopRunnable(), "Watch");
			watchThread.start();
		} catch (IOException e) {
			throw new RuntimeException("Error during initialization", e);
		}

		registerTrees();
	}

	public synchronized void close() {
		isRunning = false;
		watchThread.interrupt();
		timer.cancel();
		try {
			watchThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized void unregisterStaleWatchKeys() {
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

	private synchronized void registerDir(Path dir) {
		if (!watchKeys.containsKey(dir)) {
			log.info("Registering " + dir);

			try {
				WatchKey watchKey = dir.register(watchService,
						StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE,
						StandardWatchEventKinds.ENTRY_MODIFY,
						StandardWatchEventKinds.OVERFLOW);
				watchKeys.put(dir, watchKey);
			} catch (IOException e) {
				log.debug("Error while registering watch key", e);
			}
		}
	}

	private synchronized void registerTrees() {
		for (Path root : rootDirs) {
			registerTree(root);
		}
	}

	private synchronized void registerTree(Path root) {
		try {
			Files.walkFileTree(root, new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					registerDir(dir);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file,
						IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(
					"error while registering directory tree " + root, e);
		}
	}

	private synchronized void timerElapsed() {
		registerTrees();
		unregisterStaleWatchKeys();
		listener.run();
	}

	private final class WatchLoopRunnable implements Runnable {

		@Override
		public void run() {
			while (isRunning) {
				WatchKey key;
				try {
					// get next event, drop it
					key = watchService.take();
					key.pollEvents();
					key.reset();

					// reset timer task
					if (task != null) {
						task.cancel();
					}
					task = new TimerTask() {

						@Override
						public void run() {
							timerElapsed();
						}
					};
					timer.schedule(task, settleDelayMs);

				} catch (InterruptedException e) {
					isRunning = false;
				}
			}
		}
	}
}
