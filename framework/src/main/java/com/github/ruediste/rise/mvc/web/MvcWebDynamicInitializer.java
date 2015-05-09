package com.github.ruediste.rise.mvc.web;

import javax.inject.Inject;

import com.github.ruediste.rise.util.Initializer;

public class MvcWebDynamicInitializer implements Initializer {

	@Inject
	MvcWebConfiguration config;

	@Override
	public void initialize() {
		config.initialize();
	}

}
