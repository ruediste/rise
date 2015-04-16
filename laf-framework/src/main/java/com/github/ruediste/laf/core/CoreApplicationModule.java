package com.github.ruediste.laf.core;

import javax.inject.Named;
import javax.inject.Singleton;

import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.laf.core.front.reload.ClassSpaceCache;
import com.github.ruediste.laf.core.front.reload.DynamicSpace;
import com.github.ruediste.laf.core.front.reload.PermanentSpace;
import com.github.ruediste.laf.core.front.reload.SpaceAwareClassLoader;
import com.github.ruediste.laf.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;

public class CoreApplicationModule extends AbstractModule {

	@Override
	protected void configure() throws Exception {
		bind(ClassHierarchyCache.class).asEagerSingleton();
		InitializerUtil.register(config(), CoreApplicationInitializer.class);
	}

	@Named("dynamic")
	@Provides
	@Singleton
	SpaceAwareClassLoader spaceAwareClassLoaderDynamic(ClassSpaceCache cache) {
		return new SpaceAwareClassLoader(Thread.currentThread()
				.getContextClassLoader(), DynamicSpace.class, cache,
				PermanentSpace.class);
	}
}