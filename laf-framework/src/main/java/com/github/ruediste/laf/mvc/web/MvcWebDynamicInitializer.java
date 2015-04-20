package com.github.ruediste.laf.mvc.web;

import javax.inject.Inject;

import com.github.ruediste.laf.util.Initializer;

public class MvcWebDynamicInitializer implements Initializer {

	@Inject
	MvcWebConfiguration config;

	@Override
	public void initialize() {
		config.initialize();
	}

}
