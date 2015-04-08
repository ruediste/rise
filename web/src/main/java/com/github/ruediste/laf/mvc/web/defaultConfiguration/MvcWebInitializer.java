package com.github.ruediste.laf.mvc.web.defaultConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.laf.core.base.InitializerBase;
import com.github.ruediste.laf.core.defaultConfiguration.CoreInitializer;

@Singleton
public class MvcWebInitializer extends InitializerBase {

	@Inject
	CoreInitializer coreInitializer;

	@Override
	protected void initializeImpl() {
		coreInitializer.initialize();
	}

}
