package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.base.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;

public class MvcWebApplicationInstanceModule extends AbstractModule {

	@Override
	protected void configure() throws Exception {
		InitializerUtil.register(config(),
				MvcWebApplicationInstanceInitializer.class);
	}
}
