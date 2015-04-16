package com.github.ruediste.laf.core;

import javax.inject.Inject;

import com.github.ruediste.laf.util.Initializer;

public class CoreApplicationInstanceInitializer implements Initializer {

	@Inject
	CoreConfiguration config;

	@Override
	public void initialize() {
		config.initialize();
	}

}
