package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.laf.core.front.reload.DynamicModule;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcWebDynamicModule extends DynamicModule {

	public MvcWebDynamicModule(Injector permanentInjector) {
		super(permanentInjector);
	}

	@Override
	protected void configure() throws Exception {
		bindToPermanentInjector(ClassHierarchyCache.class);
	}
}
