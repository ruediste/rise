package com.github.ruediste.laf.integration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteStreams;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;

public class IntegrationClassLoader extends ClassLoader {
	HashSet<String> notDelegated;

	static {
		registerAsParallelCapable();
	}

	HashMap<String, byte[]> byteCode = new HashMap<>();

	public IntegrationClassLoader(ClassLoader parent, Class<?>... notDelegated) {
		super(parent);
		this.notDelegated = new HashSet<>();
		for (Class<?> cls : notDelegated) {
			this.notDelegated.add(cls.getName());
		}

		try {
			for (ResourceInfo resource : ClassPath.from(parent).getResources()) {
				if (resource.getResourceName().startsWith(
						"com/github/ruediste/laf/integration/resources")) {
					try (ZipInputStream in = new ZipInputStream(resource.url()
							.openStream())) {
						ZipEntry entry = in.getNextEntry();
						while (entry != null) {
							if (entry.getName().endsWith(".class")) {
								String name = entry.getName().replace('/', '.');
								name = name.substring(0, name.length()
										- ".class".length());
								byte[] bytes = ByteStreams.toByteArray(in);
								byteCode.put(name, bytes);
							}
							entry = in.getNextEntry();
						}
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
		byte[] bb = byteCode.get(name);
		if (bb != null) {
			return defineClass(name, bb, 0, bb.length);
		}
		return super.findClass(name);
	}
}
