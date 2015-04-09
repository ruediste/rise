package com.github.ruediste.laf.core.entry;

import javax.inject.Named;

import com.github.ruediste.laf.core.CoreApplicationModule;
import com.github.ruediste.laf.core.base.InitializerUtil;
import com.github.ruediste.laf.core.classReload.*;
import com.github.ruediste.laf.mvc.web.MvcWebApplicationModule;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;

/**
 * Module configuring the permanent injector for LAF
 */
public class ApplicationModule extends AbstractModule {

	@Override
	protected void configure() {
		InitializerUtil.register(config(), ApplicationInitializer.class);
		bind(ClassSpaceCache.class).asEagerSingleton();
		install(new MvcWebApplicationModule());
		install(new CoreApplicationModule());
	}

	@Named("dynamic")
	@Provides
	SpaceAwareClassLoader spaceAwareClassLoaderDynamic(ClassSpaceCache cache) {
		return new SpaceAwareClassLoader(Thread.currentThread()
				.getContextClassLoader(), DynamicSpace.class, cache,
				PermanentSpace.class);
	}
}