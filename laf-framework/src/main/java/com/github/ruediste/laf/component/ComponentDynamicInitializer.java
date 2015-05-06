package com.github.ruediste.laf.component;

import javax.inject.Inject;

import com.github.ruediste.laf.util.Initializer;

public class ComponentDynamicInitializer implements Initializer {

	@Inject
	ComponentConfiguration config;

	@Override
	public void initialize() {
		config.initialize();
	}
}
