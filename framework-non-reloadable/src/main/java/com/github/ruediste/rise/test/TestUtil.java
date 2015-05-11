package com.github.ruediste.rise.test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Utility class for unit tests
 */
public class TestUtil {

	private TestUtil() {
	}

	/**
	 * Delete a directory tree recursively
	 */
	public static void deleteDirTree(Path directory) throws IOException {
		java.nio.file.Files.walkFileTree(directory,
				new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {
						java.nio.file.Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir,
							IOException exc) throws IOException {
						java.nio.file.Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}
				});
	}
}
