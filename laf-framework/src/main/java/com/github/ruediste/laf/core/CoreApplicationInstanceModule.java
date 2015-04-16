package com.github.ruediste.laf.core;

import com.github.ruediste.laf.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;

public class CoreApplicationInstanceModule extends AbstractModule {

	@Override
	protected void configure() throws Exception {
		InitializerUtil.register(config(),
				CoreApplicationInstanceInitializer.class);
	}

}
