package com.github.ruediste.laf.mvc.web.defaultConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.laf.core.base.Initializer;
import com.github.ruediste.laf.core.defaultConfiguration.CoreInitializer;

@Singleton
public class MvcWebInitializer extends Initializer {

	@Inject
	CoreInitializer coreInitializer;

	@Override
	protected void initialize() {
		coreInitializer.initialize();
	}

}
