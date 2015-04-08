package com.github.ruediste.laf.core.base;

import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Static factory for bean instances, for situations where dependency injection
 * is not available.
 */
public class InstanceFactory {
	private static ThreadLocal<Injector> injector;

	public static <T> T getInstance(Class<T> clazz) {
		return injector.get().getInstance(clazz);
	}

	public static <T> T getInstance(CoreDependencyKey<T> key) {
		return injector.get().getInstance(key);
	}

	public static void setInjector(Injector injector) {
		InstanceFactory.injector.set(injector);
	}

	public static void removeInjector() {
		InstanceFactory.injector.remove();
	}
}
