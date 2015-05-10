package com.github.ruediste.rise.mvc;

import com.github.ruediste.rise.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.rise.core.front.reload.DynamicModule;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcDynamicModule extends DynamicModule {

	public MvcDynamicModule(Injector permanentInjector) {
		super(permanentInjector);
	}

	@Override
	protected void configure() throws Exception {
		bindToPermanentInjector(ClassHierarchyCache.class);
		InitializerUtil.register(config(), MvcDynamicInitializer.class);
	}
}
