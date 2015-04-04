package com.github.ruediste.laf.core.classReload;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;

import org.slf4j.Logger;

/**
 * Watches all directories in a directory tree for changes
 */
public class DirectoryTreeWatcher {

	Logger log;
	private WatchService watchService;
	private ChangeListener listener;

	private HashSet<Path> watchedDirs = new HashSet<>();

	public interface ChangeListener {
		void created(Path path);

		void deleted(Path path);

		void modified(Path path);

		void overflowed(Path path);
	}

	public void initialize(ChangeListener listener) {
		this.listener = listener;

		// create watch service
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// start polling thread
		new Thread(new EventPoller(), "Directory Poller").start();
	}

	/**
	 * Recursively register all directories in the tree starting at root
	 */
	public synchronized void registerDirectoryTree(Path root) {
		// register directories with watch service
		try {
			Files.walkFileTree(root.normalize(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					log.debug("registering directory " + dir);
					registerDir(dir);
					return super.preVisitDirectory(dir, attrs);
				}

			});
		} catch (IOException e) {
			throw new RuntimeException("Error while registring watchers", e);
		}
	}

	/**
	 * Register a single directory
	 */
	private synchronized void registerDir(Path dir) {
		if (watchedDirs.add(dir)) {
			try {
				dir.register(watchService,
						StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE,
						StandardWatchEventKinds.ENTRY_MODIFY);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Close this watchers. After the method returns, no more change events will
	 * be sent.
	 */
	public synchronized void close() {
		closed = true;
		try {
			watchService.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void processEvents(WatchKey key) {
		Path rootPath = (Path) key.watchable();
		for (WatchEvent<?> event : key.pollEvents()) {
			if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
				Path path = rootPath.resolve((Path) event.context());

				if (Files.isDirectory(path)) {
					synchronized (this) {
						log.debug("Watching directory " + path);
						registerDirectoryTree(path);
					}
				}
				synchronized (this) {
					if (!closed) {
						listener.created(path);
					}
				}
			}
			if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
				Path path = rootPath.resolve((Path) event.context());
				synchronized (this) {
					if (!closed) {
						listener.deleted(path);
					}
				}
			}
			if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
				Path path = rootPath.resolve((Path) event.context());
				synchronized (this) {
					if (!closed) {
						listener.modified(path);
					}
				}
			}
			if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
				Path path = rootPath.resolve((Path) event.context());
				synchronized (this) {
					if (!closed) {
						listener.overflowed(path);
					}
				}
			}
		}
	}

	private volatile boolean closed;

	private class EventPoller implements Runnable {

		@Override
		public void run() {
			while (!closed) {
				try {
					WatchKey key = watchService.take();
					processEvents(key);
					key.reset();
				} catch (InterruptedException | ClosedWatchServiceException e) {
					// break loop
					return;
				} catch (Throwable t) {
					log.warn("error in watch thread", t);
					// continue loop
				}
			}
		}

	}
}
