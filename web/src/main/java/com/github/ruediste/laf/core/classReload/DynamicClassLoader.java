package com.github.ruediste.laf.core.classReload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.PrimitiveIterator.OfInt;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.classReload.DirectoryTreeWatcher.ChangeListener;
import com.github.ruediste.laf.core.classReload.Scanner.ScannerListener;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

public class DynamicClassLoader extends ClassLoader {
	Logger log;

	@Inject
	Scanner scanner;

	@Inject
	DirectoryTreeWatcher watcher;

	public static final String DYNAMIC_FILE = "META-INF/com.github.ruediste.dynamicProject";

	private ClassLoader parent;

	private final class NotifyingChangeListener implements ChangeListener {
		@Override
		public void modified(Path path) {
			onChange.run();
		}

		@Override
		public void deleted(Path path) {
			onChange.run();
		}

		@Override
		public void created(Path path) {
			onChange.run();
		}

		@Override
		public void overflowed(Path path) {
			onChange.run();
		}
	}

	static class DynamicSource {
		Path path;
		Set<Pattern> exclusionPatterns = new HashSet<>();
		public String project;

		void addExclusion(String pattern) {
			StringBuilder sb = new StringBuilder();
			OfInt it = pattern.codePoints().iterator();
			while (it.hasNext()) {
				int cp = it.next();
				if (cp == '*') {
					if (it.hasNext()) {
						cp = it.next();
						if (cp == '*') {
							sb.append(".*");
						} else {
							sb.append("[^.]*");
							addEscaped(sb, cp);
						}
					}
				} else
					addEscaped(sb, cp);
			}
			exclusionPatterns.add(Pattern.compile(sb.toString()));
		}

		private void addEscaped(StringBuilder sb, int cp) {
			switch (cp) {
			case '.':
			case '?':
			case '[':
			case ']':
			case '{':
			case '}':
			case '+':
			case '!':
				sb.append("\\");
				sb.appendCodePoint(cp);
				break;

			default:
				sb.appendCodePoint(cp);
				break;
			}
		}

		boolean isExcluded(String name) {
			for (Pattern pattern : exclusionPatterns) {
				if (pattern.matcher(name).matches()) {
					return true;
				}
			}
			return false;
		}
	}

	Set<DynamicSource> dynamicRootDirectories = new HashSet<>();
	Set<DynamicSource> dynamicJars = new HashSet<>();

	Object dummyValue = new Object();

	private Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<String, Class<?>>();

	static {
		ClassLoader.registerAsParallelCapable();
	}

	private Runnable onChange;

	public void initialize(Set<String> dynamicProjects, Runnable onChange) {
		this.onChange = onChange;

		this.parent = Thread.currentThread().getContextClassLoader();
		NotifyingChangeListener changeListener = new NotifyingChangeListener();
		watcher.initialize(changeListener);

		scanner.initialize(new ScannerListener() {

			@Override
			public void foundRootDirectory(Path directory,
					ClassLoader classloader) {
				Path dynamicFile = directory.resolve(DYNAMIC_FILE);
				if (Files.exists(dynamicFile)) {
					try {
						DynamicSource source = createDynamicSource(directory,
								new FileInputStream(dynamicFile.toFile()));
						if (dynamicProjects.contains(source.project)) {
							dynamicRootDirectories.add(source);
							watcher.registerDirectoryTree(directory);
						}
					} catch (IOException e) {
						log.warn("error while reading project file", e);
					}
				}
			}

			@Override
			public void foundJarFile(Path path, JarFile jarFile,
					ClassLoader classloader) {
				ZipEntry entry = jarFile.getEntry(DYNAMIC_FILE);
				if (entry != null) {
					try {
						DynamicSource source = createDynamicSource(path,
								jarFile.getInputStream(entry));
						if (dynamicProjects.contains(source.project)) {
							dynamicJars.add(source);
							Path dir = path.getParent();
							watcher.registerDirectory(dir);
						}
					} catch (IOException e) {
						log.warn("error while reading project file from jar", e);
					}
				}
			}
		});
		scanner.scan(parent);

		// scanner can be GCed
		scanner = null;

		// start watching thread
		new Thread(new Runnable() {

			@Override
			public void run() {

			}
		}).start();
	}

	private volatile boolean closed;

	public synchronized void close() {
		if (!closed) {
			closed = true;
			watcher.close();
		}
	}

	private DynamicSource createDynamicSource(Path path, InputStream input) {
		DynamicSource result = new DynamicSource();
		Properties props = new Properties();
		try {
			props.load(input);
		} catch (IOException e) {
			throw new RuntimeException("error while loading properties", e);
		}
		result.path = path;
		result.project = props.getProperty("project").trim();
		Splitter.on(',').omitEmptyStrings()
				.split(Strings.nullToEmpty(props.getProperty("exclude")))
				.forEach(x -> result.addExclusion(x));
		return result;
	}

	static final String CLASS_FILE_NAME_EXTENSION = ".class";

	static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ")
			.omitEmptyStrings();

	@Override
	public Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class<?> result;
		synchronized (getClassLoadingLock(name)) {
			result = loadedClasses.get(name);
			if (result == null) {

				String fileName = name.replace('.', '/') + ".class";

				// try loading from directories
				for (DynamicSource dir : dynamicRootDirectories) {
					if (dir.isExcluded(name))
						continue;
					try {
						Path classFile = dir.path.resolve(fileName);
						if (Files.exists(classFile)) {
							byte[] bb = Files.readAllBytes(classFile);
							result = defineClass(name, bb, 0, bb.length);
							break;
						}
					} catch (IOException e) {
						log.warn("error while loading class, ignoring", e);
					}
				}

				// try loading from jar files
				if (result == null) {
					for (DynamicSource jarSource : dynamicJars) {
						if (jarSource.isExcluded(name))
							continue;
						JarFile jar = null;
						try {
							jar = new JarFile(jarSource.path.toFile());
							ZipEntry entry = jar.getEntry(fileName);
							if (entry != null) {
								byte[] bb = ByteStreams.toByteArray(jar
										.getInputStream(entry));
								result = defineClass(name, bb, 0, bb.length);
								break;
							}
						} catch (IOException e) {
							log.warn("unable to load jar or jar entry", e);
						} finally {
							if (jar != null)
								try {
									jar.close();
								} catch (IOException e) {
									log.warn("error wile closing", e);
								}
						}
					}
				}

				if (result != null) {
					// found a dynamic class
					log.debug("loaded class " + name);
					loadedClasses.put(name, result);
				} else if (parent != null) {
					// ask parent
					result = parent.loadClass(name);
				}

				// resolve class if necessary
				if (result != null && resolve)
					resolveClass(result);
			}
		}
		return result;
	}

}
