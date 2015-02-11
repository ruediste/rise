package com.github.ruediste.laf.core.entry;

import com.github.ruediste.laf.core.guice.LoggerBindingModule;
import com.google.inject.AbstractModule;

public class ApplicationModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new LoggerBindingModule());
	}

}