package com.github.ruediste.laf.core.base;

import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * Static factory for bean instances, for situations where dependency injection
 * is not available.
 */
public class InstanceFactory {
	private static ThreadLocal<Injector> injector;

	public static <T> T getInstance(Class<T> clazz) {
		return injector.get().getInstance(clazz);
	}

	public static <T> T getInstance(Key<T> key) {
		return injector.get().getInstance(key);
	}

	public static void setInjector(Injector injector) {
		InstanceFactory.injector.set(injector);
	}

	public static void removeInjector() {
		InstanceFactory.injector.remove();
	}
}
