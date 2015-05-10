package com.github.ruediste.rise.core.front.reload;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

/**
 * Class loader loading all {@link Reloadable @Reloadable} classes by itself and
 * delegates to the parent class loader for other classes
 */
public class ReloadableClassLoader extends ClassLoader {

	private ReloadebleClassesIndex index;

	public ReloadableClassLoader(ClassLoader parent,
			ReloadebleClassesIndex index) {
		super(parent);
		this.index = index;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		{
			Class<?> result = findLoadedClass(name);
			if (result != null) {
				if (resolve)
					resolveClass(result);
				return result;
			}
		}
		if (!name.startsWith("java.")) {
			if (index.isReloadable(name)) {
				Class<?> result;
				String resouceName = name.replace('.', '/') + ".class";
				try (InputStream in = getResourceAsStream(resouceName)) {
					byte[] bb = ByteStreams.toByteArray(in);
					result = defineClass(name, bb, 0, bb.length);
				} catch (IOException e) {
					throw new RuntimeException("Error while loading "
							+ resouceName, e);
				}
				if (resolve) {
					resolveClass(result);
				}
				return result;
			}

		}

		Class<?> result = getParent().loadClass(name);
		if (resolve) {
			resolveClass(result);
		}
		return result;
	}
}
