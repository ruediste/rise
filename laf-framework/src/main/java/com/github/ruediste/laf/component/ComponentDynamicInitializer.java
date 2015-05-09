package com.github.ruediste.laf.component;

import javax.inject.Inject;

import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.util.Initializer;

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
