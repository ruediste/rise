package com.github.ruediste.laf.api;

import javax.servlet.ServletConfig;

import com.github.ruediste.laf.core.CorePermanentModule;
import com.github.ruediste.laf.core.front.LoggerModule;
import com.github.ruediste.salta.jsr330.AbstractModule;

public class PermanentApplicationModule extends AbstractModule {

	protected ServletConfig servletConfig;

	public PermanentApplicationModule(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	@Override
	protected void configure() throws Exception {

		installCoreModule();
		installLoggerModule();
	}

	protected void installCoreModule() {
		install(new CorePermanentModule(servletConfig));
	}

	protected void installLoggerModule() {
		install(new LoggerModule());
	}

}
