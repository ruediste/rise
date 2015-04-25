package com.github.ruediste.laf.integration;

import javax.servlet.ServletConfig;

import com.github.ruediste.laf.api.PermanentApplicationModule;
import com.github.ruediste.salta.jsr330.AbstractModule;

/**
 * Module configuring the permanent injector for LAF
 */
public class PermanentIntegrationModule extends AbstractModule {

	private ServletConfig servletConfig;

	public PermanentIntegrationModule(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	@Override
	protected void configure() {
		install(new PermanentApplicationModule(servletConfig));
	}

}