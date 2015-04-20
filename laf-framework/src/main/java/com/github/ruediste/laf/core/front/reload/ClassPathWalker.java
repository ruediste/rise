package com.github.ruediste.laf.core.front.reload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;

/**
 * Wald the classpath and notify a {@link ClassPathVisitor} of all root
 * directories, jar files, classes and resouces present.
 * 
 * <p>
 * Based on Guava's {@link ClassPath}
 * </p>
 */
public class ClassPathWalker {
	private static Logger logger = Logger.getLogger(ClassPathWalker.class
			.getName());

	public enum ClassPathVisitResult {
		/**
		 * continue scanning this root directory or jar file
		 * 
		 * @see ClassPathVisitor#visitRootDirectory
		 * @see ClassPathVisitor#visitJarFile
		 */
		CONTINUE,

		/**
		 * Skip this root directory or jar file
		 * 
		 * @see ClassPathVisitor#visitRootDirectory
		 * @see ClassPathVisitor#visitJarFile
		 */
		SKIP_CONTENTS,
	}

	public interface ClassPathVisitor {
		/**
		 * Visit a classpath root directory
		 */
		ClassPathVisitResult visitRootDirectory(Path rootDirectory,
				ClassLoader classloader);

		/**
		 * Visit a Jar File on the classpath
		 */
		ClassPathVisitResult visitJarFile(Path path, JarFile jarFile,
				ClassLoader classloader);

		/**
		 * Visit a resource
		 */
		void visitResource(String name, ClassLoader classLoader,
				Supplier<InputStream> inputStreamSupplier);

		/**
		 * Visit a class
		 */
		void visitClass(String className, ClassLoader classLoader,
				Supplier<InputStream> inputStreamSupplier);

	}

	public static class SimpleClassPathVisitor implements ClassPathVisitor {

		@Override
		public ClassPathVisitResult visitRootDirectory(Path rootDirectory,
				ClassLoader classloader) {
			return ClassPathVisitResult.CONTINUE;
		}

		@Override
		public ClassPathVisitResult visitJarFile(Path path, JarFile jarFile,
				ClassLoader classloader) {
			return ClassPathVisitResult.CONTINUE;
		}

		@Override
		public void visitResource(String name, ClassLoader classLoader,
				Supplier<InputStream> inputStreamSupplier) {
		}

		@Override
		public void visitClass(String className, ClassLoader classLoader,
				Supplier<InputStream> inputStreamSupplier) {
		}

	}

	ClassPathWalker() {
	}

	private final Set<URI> scannedUris = Sets.newHashSet();

	public static void scan(ClassLoader classloader, ClassPathVisitor visitor) {
		ClassPathWalker walker = new ClassPathWalker();
		for (Map.Entry<URI, ClassLoader> entry : getClassPathEntries(
				classloader).entrySet()) {
			try {
				walker.scan(entry.getKey(), entry.getValue(), visitor);
			} catch (IOException e) {
				throw new RuntimeException("Error while scanning "
						+ entry.getKey());
			}
		}
	}

	@VisibleForTesting
	static ImmutableMap<URI, ClassLoader> getClassPathEntries(
			ClassLoader classloader) {
		LinkedHashMap<URI, ClassLoader> entries = Maps.newLinkedHashMap();
		// Search parent first, since it's the order ClassLoader#loadClass()
		// uses.
		ClassLoader parent = classloader.getParent();
		if (parent != null) {
			entries.putAll(getClassPathEntries(parent));
		}
		if (classloader instanceof URLClassLoader) {
			URLClassLoader urlClassLoader = (URLClassLoader) classloader;
			for (URL entry : urlClassLoader.getURLs()) {
				URI uri;
				try {
					uri = entry.toURI();
				} catch (URISyntaxException e) {
					throw new IllegalArgumentException(e);
				}
				if (!entries.containsKey(uri)) {
					entries.put(uri, classloader);
				}
			}
		}
		return ImmutableMap.copyOf(entries);
	}

	void scan(URI uri, ClassLoader classloader, ClassPathVisitor visitor)
			throws IOException {
		if (uri.getScheme().equals("file") && scannedUris.add(uri)) {
			scanFrom(new File(uri), classloader, visitor);
		}
	}

	@VisibleForTesting
	void scanFrom(File file, ClassLoader classloader, ClassPathVisitor visitor)
			throws IOException {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			scanDirectory(file, classloader, visitor);
		} else {
			scanJar(file, classloader, visitor);
		}
	}

	private void scanDirectory(File directory, ClassLoader classloader,
			ClassPathVisitor visitor) throws IOException {
		if (visitor.visitRootDirectory(directory.toPath(), classloader) == ClassPathVisitResult.CONTINUE)
			scanDirectory(directory, classloader, "", ImmutableSet.<File> of(),
					visitor);
	}

	private void scanDirectory(File directory, ClassLoader classloader,
			String packagePrefix, ImmutableSet<File> ancestors,
			ClassPathVisitor visitor) throws IOException {
		File canonical = directory.getCanonicalFile();
		if (ancestors.contains(canonical)) {
			// A cycle in the filesystem, for example due to a symbolic link.
			return;
		}
		File[] files = directory.listFiles();
		if (files == null) {
			logger.warning("Cannot read directory " + directory);
			// IO error, just skip the directory
			return;
		}
		ImmutableSet<File> newAncestors = ImmutableSet.<File> builder()
				.addAll(ancestors).add(canonical).build();
		for (File f : files) {
			String name = f.getName();
			if (f.isDirectory()) {
				scanDirectory(f, classloader, packagePrefix + name + "/",
						newAncestors, visitor);
			} else {
				String resourceName = packagePrefix + name;
				if (!resourceName.equals(JarFile.MANIFEST_NAME)) {
					if (resourceName.endsWith(CLASS_FILE_NAME_EXTENSION))
						visitor.visitClass(getClassName(resourceName),
								classloader, () -> classloader
										.getResourceAsStream(resourceName));
					else
						visitor.visitResource(resourceName, classloader,
								() -> classloader
										.getResourceAsStream(resourceName));
				}
			}
		}
	}

	private void scanJar(File file, ClassLoader classloader,
			ClassPathVisitor visitor) throws IOException {
		JarFile jarFile;
		try {
			jarFile = new JarFile(file);
		} catch (IOException e) {
			// Not a jar file
			return;
		}

		if (visitor.visitJarFile(file.toPath(), jarFile, classloader) == ClassPathVisitResult.SKIP_CONTENTS)
			return;

		try {
			for (URI uri : getClassPathFromManifest(file, jarFile.getManifest())) {
				scan(uri, classloader, visitor);
			}
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory()
						|| entry.getName().equals(JarFile.MANIFEST_NAME)) {
					continue;
				}
				Supplier<InputStream> inputStreamSupplier = () -> {
					try {
						return jarFile.getInputStream(entry);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				};
				if (entry.getName().endsWith(CLASS_FILE_NAME_EXTENSION)) {
					visitor.visitClass(getClassName(entry.getName()),
							classloader, inputStreamSupplier);
				} else
					visitor.visitResource(entry.getName(), classloader,
							inputStreamSupplier);
			}
		} finally {
			try {
				jarFile.close();
			} catch (IOException ignored) {
			}
		}
	}

	/** Separator for the Class-Path manifest attribute value in jar files. */
	private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(
			" ").omitEmptyStrings();

	/**
	 * Returns the class path URIs specified by the {@code Class-Path} manifest
	 * attribute, according to <a href=
	 * "http://docs.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Main%20Attributes"
	 * > JAR File Specification</a>. If {@code manifest} is null, it means the
	 * jar file has no manifest, and an empty set will be returned.
	 */
	@VisibleForTesting
	ImmutableSet<URI> getClassPathFromManifest(File jarFile, Manifest manifest) {
		if (manifest == null) {
			return ImmutableSet.of();
		}
		ImmutableSet.Builder<URI> builder = ImmutableSet.builder();
		String classpathAttribute = manifest.getMainAttributes().getValue(
				Attributes.Name.CLASS_PATH.toString());
		if (classpathAttribute != null) {
			for (String path : CLASS_PATH_ATTRIBUTE_SEPARATOR
					.split(classpathAttribute)) {
				URI uri;
				try {
					uri = getClassPathEntry(jarFile, path);
				} catch (URISyntaxException e) {
					// Ignore bad entry
					logger.warning("Invalid Class-Path entry: " + path);
					continue;
				}
				builder.add(uri);
			}
		}
		return builder.build();
	}

	/**
	 * Returns the absolute uri of the Class-Path entry value as specified in <a
	 * href=
	 * "http://docs.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Main%20Attributes"
	 * > JAR File Specification</a>. Even though the specification only talks
	 * about relative urls, absolute urls are actually supported too (for
	 * example, in Maven surefire plugin).
	 */
	@VisibleForTesting
	static URI getClassPathEntry(File jarFile, String path)
			throws URISyntaxException {
		URI uri = new URI(path);
		if (uri.isAbsolute()) {
			return uri;
		} else {
			return new File(jarFile.getParentFile(), path.replace('/',
					File.separatorChar)).toURI();
		}
	}

	private static final String CLASS_FILE_NAME_EXTENSION = ".class";

	@VisibleForTesting
	static String getClassName(String filename) {
		int classNameEnd = filename.length()
				- CLASS_FILE_NAME_EXTENSION.length();
		return filename.substring(0, classNameEnd).replace('/', '.');
	}
}