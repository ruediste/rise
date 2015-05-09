package com.github.ruediste.rise.component;

import javax.inject.Inject;

import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.util.Initializer;

public class ComponentDynamicInitializer implements Initializer {

	@Inject
	ComponentConfiguration config;

	@Inject
	PathInfoIndex index;

	@Override
	public void initialize() {
		config.initialize();
		index.registerPathInfo(config.getReloadPath(), config.getReloadParser());
	}
}
