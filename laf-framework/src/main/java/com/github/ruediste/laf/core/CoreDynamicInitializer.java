package com.github.ruediste.laf.core;

import javax.inject.Inject;

import com.github.ruediste.laf.util.Initializer;

public class CoreDynamicInitializer implements Initializer {

	@Inject
	CoreConfiguration config;

	@Override
	public void initialize() {
		config.initialize();
		config.dynamicClassLoader = Thread.currentThread()
				.getContextClassLoader();
	}

}
