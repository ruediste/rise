package com.github.ruediste.laf.integration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;

import com.google.common.io.ByteStreams;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;

public class IntegrationClassLoader extends ClassLoader {
	private static final String EXT = ".rsc";

	private static final String PREFIX = "com/github/ruediste/laf/integration/resources/";

	HashSet<String> notDelegated;

	static {
		registerAsParallelCapable();
	}

	HashMap<String, String> resources = new HashMap<>();

	public IntegrationClassLoader(ClassLoader parent, Class<?>... notDelegated) {
		super(parent);
		this.notDelegated = new HashSet<>();
		for (Class<?> cls : notDelegated) {
			this.notDelegated.add(cls.getName());
		}

		try {
			TreeSet<String> rootPaths = new TreeSet<>();
			Properties deps = new Properties();
			deps.load(getClass().getResourceAsStream("dependencies.properties"));
			for (Entry<Object, Object> entry : deps.entrySet()) {
				String key = (String) entry.getKey();
				if (key.endsWith("/version")) {
					key = key.substring(0, key.length() - "/version".length());
					key = key.replace('.', '/');
					key = key + "/" + entry.getValue() + "/";
					rootPaths.add(PREFIX + key);
				}
			}

			for (ResourceInfo resource : ClassPath.from(parent).getResources()) {
				String name = resource.getResourceName();
				if (name.startsWith(PREFIX) && name.endsWith(EXT)) {
					String root = rootPaths.floor(name);
					if (name.startsWith(root)) {
						String s = name.substring(root.length());
						s = s.substring(0, s.length() - EXT.length());
						resources.put(s, name);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		if (notDelegated.contains(name)) {
			try (InputStream in = getParent().getResourceAsStream(
					name.replace('.', '/') + ".class")) {
				byte[] bb = ByteStreams.toByteArray(in);
				Class<?> result = defineClass(name, bb, 0, bb.length);
				if (resolve)
					resolveClass(result);
				return result;

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		return super.loadClass(name, resolve);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String s = name.replace('.', '/') + ".class";
		String resource = resources.get(s);
		if (resource != null) {
			InputStream in = getParent().getResourceAsStream(resource);
			if (in != null) {
				try {
					byte[] bb = ByteStreams.toByteArray(in);
					return defineClass(name, bb, 0, bb.length);
				} catch (IOException e) {
					throw new RuntimeException("Error reading class", e);
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						throw new RuntimeException("Error closing stream", e);
					}
				}
			}
		}
		return super.findClass(name);
	}
}
