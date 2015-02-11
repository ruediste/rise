package com.github.ruediste.laf.core.classReload;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Scan the classpath and notify a listener of all root directories and jar files found
 */
public class Scanner {
	Logger log;

	public interface ScannerListener {
		void foundJarFile(Path path, JarFile jarFile, ClassLoader classloader);
		void foundRootDirectory(Path rootDirectory, ClassLoader classloader);
	}

	private final Set<URI> scannedUris = Sets.newHashSet();
	private ScannerListener listener;
 
	public void initialize (ScannerListener listener) {
		this.listener = listener;
	}
	public void scan(ClassLoader cl) {
		if (cl.getParent() != null)
			scan(cl.getParent());

		if (cl instanceof URLClassLoader) {
			for (URL url : ((URLClassLoader) cl).getURLs()) {
				try {
					URI uri = url.toURI();
					scan(uri, cl);
				} catch (URISyntaxException | IOException e) {
					log.warn("Error while scanning classpath, ignoring", e);
				}

			}
		}
	}

	void scan(URI uri, ClassLoader classloader) throws IOException {
		if (uri.getScheme().equals("file") && scannedUris.add(uri)) {
			scanFrom(new File(uri), classloader);
		}
	}

	void scanFrom(File file, ClassLoader classloader) throws IOException {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			listener.foundRootDirectory(file.toPath().normalize(), classloader);
		} else {
			scanJar(file, classloader);
		}
	}

	private void scanJar(File file, ClassLoader classloader) throws IOException {
		JarFile jarFile;
		try {
			jarFile = new JarFile(file);
		} catch (IOException e) {
			// Not a jar file
			return;
		}
		listener.foundJarFile(file.toPath().normalize(), jarFile, classloader);
		try {
			for (URI uri : getClassPathFromManifest(file, jarFile.getManifest())) {
				scan(uri, classloader);
			}
		} finally {
			try {
				jarFile.close();
			} catch (IOException ignored) {
			}
		}
	}

	/**
	 * Returns the class path URIs specified by the {@code Class-Path} manifest
	 * attribute, according to <a href=
	 * "http://docs.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Main%20Attributes"
	 * > JAR File Specification</a>. If {@code manifest} is null, it means the
	 * jar file has no manifest, and an empty set will be returned.
	 */
	ImmutableSet<URI> getClassPathFromManifest(File jarFile, Manifest manifest) {
		if (manifest == null) {
			return ImmutableSet.of();
		}
		ImmutableSet.Builder<URI> builder = ImmutableSet.builder();
		String classpathAttribute = manifest.getMainAttributes().getValue(
				Attributes.Name.CLASS_PATH.toString());
		if (classpathAttribute != null) {
			for (String path : DynamicClassLoader.CLASS_PATH_ATTRIBUTE_SEPARATOR
					.split(classpathAttribute)) {
				URI uri;
				try {
					uri = getClassPathEntry(jarFile, path);
				} catch (URISyntaxException e) {
					// Ignore bad entry
					log.warn("Invalid Class-Path entry: " + path);
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
	URI getClassPathEntry(File jarFile, String path) throws URISyntaxException {
		URI uri = new URI(path);
		if (uri.isAbsolute()) {
			return uri;
		} else {
			return new File(jarFile.getParentFile(), path.replace('/',
					File.separatorChar)).toURI();
		}
	}
}