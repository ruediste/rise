package com.github.ruediste.laf.core.classReload;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.slf4j.Logger;

public class FileChangeNotifier {

	@Inject
	Logger log;

	@Inject
	DirectoryChangeWatcher watcher;

	Set<Consumer<FileChangeTransaction>> listeners = new LinkedHashSet<>();
	Map<Path, FileTime> currentPaths = new HashMap<>();
	private Set<Path> rootDirs = new HashSet<>();

	public static class FileChangeTransaction {
		Set<Path> removedFiles;
		Set<Path> addedFiles;
		Set<Path> modifiedFiles;

		@Override
		public String toString() {
			return "added: " + addedFiles + "\nremoved: " + removedFiles
					+ "\nmodified: " + modifiedFiles;
		}
	}

	public synchronized void addListener(
			Consumer<FileChangeTransaction> listener) {
		listeners.add(listener);
	}

	public synchronized void removeListener(
			Consumer<FileChangeTransaction> listener) {
		listeners.remove(listener);
	}

	public void start(Set<Path> rootDirs, long settleDelayMs) {
		this.rootDirs = rootDirs;
		watcher.start(rootDirs, () -> changeOccurred(), settleDelayMs);
		changeOccurred();
	}

	public synchronized void changeOccurred() {
		HashMap<Path, FileTime> newPaths = new HashMap<>();
		FileChangeTransaction trx = new FileChangeTransaction();
		trx.addedFiles = new HashSet<>();
		trx.modifiedFiles = new HashSet<>();
		trx.removedFiles = new HashSet<>(currentPaths.keySet());

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
						} else if (!lastTime.equals(currentTime)) {
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
		for (Consumer<FileChangeTransaction> listener : listeners) {
			listener.accept(trx);
		}
		currentPaths = newPaths;
	}
}
