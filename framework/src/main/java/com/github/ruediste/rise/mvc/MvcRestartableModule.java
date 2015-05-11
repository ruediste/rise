package com.github.ruediste.rise.mvc;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyCache;
import com.github.ruediste.rise.nonReloadable.front.reload.DynamicModule;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcRestartableModule extends DynamicModule {

	public MvcRestartableModule(Injector permanentInjector) {
		super(permanentInjector);
	}

	@Override
	protected void configure() throws Exception {
		bindToPermanentInjector(ClassHierarchyCache.class);
		InitializerUtil.register(config(), MvcDynamicInitializer.class);
	}
}
