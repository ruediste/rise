package com.github.ruediste.laf.core.base;

import javax.inject.Singleton;

/**
 * Base class for initializers. Makes sure, {@link #initializeImpl()} is called
 * only once. Sub classes should be declared {@link Singleton @Singleton}
 */
public abstract class InitializerBase {

	private boolean initialized;

	public final void initialize() {
		if (initialized) {
			return;
		}
		initialized = true;
		initializeImpl();
	}

	abstract protected void initializeImpl();
}
