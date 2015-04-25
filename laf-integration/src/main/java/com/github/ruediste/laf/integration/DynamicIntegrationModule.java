package com.github.ruediste.laf.integration;

import com.github.ruediste.laf.api.DynamicApplicationModule;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;

public class DynamicIntegrationModule extends AbstractModule {

	private Injector permanentInjector;

	public DynamicIntegrationModule(Injector permanentInjector) {
		this.permanentInjector = permanentInjector;
	}

	@Override
	protected void configure() throws Exception {
		install(new DynamicApplicationModule(permanentInjector));
	}

}
