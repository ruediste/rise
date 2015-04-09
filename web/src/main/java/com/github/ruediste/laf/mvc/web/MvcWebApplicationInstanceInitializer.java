package com.github.ruediste.laf.mvc.web;

import javax.inject.Inject;

import com.github.ruediste.laf.core.base.Initializer;

public class MvcWebApplicationInstanceInitializer implements Initializer {

	@Inject
	MvcWebControllerScanner scanner;

	@Override
	public void initialize() {
		scanner.registerControllers();
	}

}
