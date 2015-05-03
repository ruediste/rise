package com.github.ruediste.laf.core.front.reload;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Base class for dynamic salta modules.
 * 
 * @see PermanentModule PermanentModule for details
 */
public abstract class DynamicModule extends AbstractModule {

	protected final Injector permanentInjector;

	public DynamicModule(Injector permanentInjector) {
		this.permanentInjector = permanentInjector;
	}

	protected void bindToPermanentInjector(Class<ClassHierarchyCache> clazz) {
		bind(clazz).toProvider(() -> permanentInjector.getInstance(clazz));
	}

}