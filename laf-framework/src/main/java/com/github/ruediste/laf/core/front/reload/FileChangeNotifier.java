package com.github.ruediste.laf.core.front.reload;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.google.common.base.Joiner;

/**
 * Watches a set of directory trees for changes and raises
 * {@link FileChangeTransaction}s which allow to stay up to date on the contents
 * of the contained files.
 */
@Singleton
public class FileChangeNotifier {

	@Inject
	Logger log;

	@Inject
	DirectoryChangeWatcher watcher;

	private Set<Consumer<FileChangeTransaction>> listeners = new LinkedHashSet<>();
	private Map<Path, FileTime> currentPaths = new HashMap<>();
	private Set<Path> rootDirs = new HashSet<>();
	private boolean isStarted;

	public static class FileChangeTransaction {
		public Set<Path> removedFiles = new HashSet<>();
		public Set<Path> addedFiles = new HashSet<>();
		public Set<Path> modifiedFiles = new HashSet<>();
		public boolean isInitial;

		@Override
		public String toString() {
			return "added: " + addedFiles + "\nremoved: " + removedFiles
					+ "\nmodified: " + modifiedFiles;
		}
	}

	/**
	 * Add a listener. Thread safe. The listener will always be invoked in the
	 * AET. May not be called after the notifier has been started
	 */
	public synchronized void addListener(
			Consumer<FileChangeTransaction> listener) {
		checkNotStarted();
		listeners.add(listener);
	}

	/**
	 * Remove a listener. Thread safe.
	 */
	public synchronized void removeListener(
			Consumer<FileChangeTransaction> listener) {
		listeners.remove(listener);
	}

	/**
	 * Throw an exception if this notifier is already started
	 */
	public synchronized void checkNotStarted() {
		if (isStarted) {
			throw new IllegalStateException("Notifier is already started");
		}
	}

	public void start(Set<Path> rootDirs, long settleDelayMs) {
		start(rootDirs, settleDelayMs, trx -> {
		});
	}

	/**
	 * Starts the notifier. The files of the staticDirs will be included in the
	 * initial transaction, but won't be watched for changes.
	 */
	public void start(Set<Path> rootDirs, long settleDelayMs,
			Set<Path> staticDirs) {
		start(rootDirs, settleDelayMs, trx -> {
			for (Path dir : staticDirs) {
				try {
					Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							trx.addedFiles.add(file);
							return FileVisitResult.CONTINUE;
						}
					});
				} catch (Exception e) {
					throw new RuntimeException("Error while traversing " + dir,
							e);
				}
			}
		});
	}

	/**
	 * @param trxPostProcessor
	 *            processor to fine tune the {@link FileChangeTransaction}
	 *            before the listeners are notified
	 */
	public void start(Set<Path> rootDirs, long settleDelayMs,
			Consumer<FileChangeTransaction> trxPostProcessor) {
		log.info("Starting notifier with dirs\n{}",
				Joiner.on("\n").join(rootDirs));
		synchronized (this) {
			isStarted = true;
		}
		this.rootDirs = rootDirs;
		watcher.start(rootDirs, paths -> changeOccurred(paths, false),
				settleDelayMs);
		changeOccurred(Collections.emptySet(), true);
	}

	public synchronized void changeOccurred(Set<Path> affectedPaths,
			boolean isInitial) {
		HashMap<Path, FileTime> newPaths = new HashMap<>();
		FileChangeTransaction trx = new FileChangeTransaction();
		trx.isInitial = isInitial;
		trx.removedFiles.addAll(currentPaths.keySet());

		for (Path root : rootDirs) {
			try {
				Files.walkFileTree(root, new FileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs) throws IOException {
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {

						// retrieve times
						FileTime currentTime = Files.getLastModifiedTime(file);
						FileTime lastTime = currentPaths.get(file);

						log.debug("visiting file {}, {}->{}", file, lastTime,
								currentTime);

						// add file to new paths
						newPaths.put(file, currentTime);

						// file is present, thus remove it from the removed list
						trx.removedFiles.remove(file);

						if (lastTime == null) {
							// wasn't present last time, so it was added
							trx.addedFiles.add(file);
						} else if (affectedPaths.contains(file)
								|| !lastTime.equals(currentTime)) {
							// the modification time changed, so file was
							// modified
							trx.modifiedFiles.add(file);
						}
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

		log.debug("Triggering Trx \n{}", trx);

		{
			ArrayList<Consumer<FileChangeTransaction>> tmp;
			synchronized (this) {
				tmp = new ArrayList<>(listeners);
			}
			for (Consumer<FileChangeTransaction> listener : tmp) {
				listener.accept(trx);
			}
		}

		currentPaths = newPaths;
	}

	public void close() {
		watcher.close();
	}
}
