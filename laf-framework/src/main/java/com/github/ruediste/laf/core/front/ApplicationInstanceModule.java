package com.github.ruediste.laf.core.front;

import com.github.ruediste.laf.core.scopes.HttpScopeModule;
import com.github.ruediste.salta.jsr330.AbstractModule;

public class ApplicationInstanceModule extends AbstractModule {

	@Override
	protected void configure() throws Exception {
		install(new HttpScopeModule());
	}

}
