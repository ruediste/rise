package com.github.ruediste.laf.integration;

import com.github.ruediste.laf.core.CoreApplicationModule;
import com.github.ruediste.laf.core.front.ApplicationInitializer;
import com.github.ruediste.laf.core.front.reload.ClassSpaceCache;
import com.github.ruediste.laf.mvc.web.MvcWebPermanentModule;
import com.github.ruediste.laf.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;

/**
 * Module configuring the permanent injector for LAF
 */
public class ApplicationModule extends AbstractModule {

	@Override
	protected void configure() {
		InitializerUtil.register(config(), ApplicationInitializer.class);
		bind(ClassSpaceCache.class).asEagerSingleton();
		install(new MvcWebPermanentModule());
		install(new CoreApplicationModule());
	}

}