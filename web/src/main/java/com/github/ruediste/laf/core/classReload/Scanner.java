package com.github.ruediste.laf.core.classReload;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.common.collect.Sets;

/**
 * Scan the classpath and notify a listener of all root directories and jar
 * files found
 */
public class Scanner {
	@Inject
	Logger log;

	public interface ScannerListener {
		void foundRootDirectory(Path rootDirectory, ClassLoader classloader);
	}

	private final Set<URI> scannedUris = Sets.newHashSet();
	private ScannerListener listener;

	public void initialize(ScannerListener listener) {
		this.listener = listener;
	}

	public void scan(ClassLoader cl) {
		if (cl.getParent() != null) {
			scan(cl.getParent());
		}

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
		}
	}

}