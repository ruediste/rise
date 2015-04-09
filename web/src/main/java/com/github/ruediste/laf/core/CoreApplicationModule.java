package com.github.ruediste.laf.core;

import com.github.ruediste.laf.core.classReload.ClassHierarchyCache;
import com.github.ruediste.salta.jsr330.AbstractModule;

public class CoreApplicationModule extends AbstractModule {

	@Override
	protected void configure() throws Exception {
		bind(ClassHierarchyCache.class).asEagerSingleton();
	}

}
