package com.github.ruediste.laf.core.entry;

import com.github.ruediste.laf.core.guice.HttpScopeModule;
import com.github.ruediste.salta.jsr330.AbstractModule;

public class ApplicationInstanceModule extends AbstractModule {

	@Override
	protected void configure() throws Exception {
		install(new HttpScopeModule());
	}

}
