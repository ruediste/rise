package com.github.ruediste.rise.mvc.web;

import com.github.ruediste.rise.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.rise.core.front.reload.DynamicModule;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcWebDynamicModule extends DynamicModule {

	public MvcWebDynamicModule(Injector permanentInjector) {
		super(permanentInjector);
	}

	@Override
	protected void configure() throws Exception {
		bindToPermanentInjector(ClassHierarchyCache.class);
		InitializerUtil.register(config(), MvcWebDynamicInitializer.class);
	}
}
