package com.github.ruediste.laf.api;

import com.github.ruediste.laf.core.CorePermanentModule;
import com.github.ruediste.laf.core.front.LoggerModule;
import com.github.ruediste.salta.jsr330.AbstractModule;

public class PermanentApplicationModule extends AbstractModule {

	@Override
	protected void configure() throws Exception {

		installCoreModule();
		installLoggerModule();
	}

	protected void installCoreModule() {
		install(new CorePermanentModule());
	}

	protected void installLoggerModule() {
		install(new LoggerModule());
	}

}
