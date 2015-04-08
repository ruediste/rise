package com.github.ruediste.laf.core.entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.laf.core.base.InitializerBase;
import com.github.ruediste.laf.mvc.web.MvcWebApplicationInitializer;

@Singleton
public class ApplicationInitializer extends InitializerBase {

	@Inject
	MvcWebApplicationInitializer mvcWebApplicationInitializer;

	@Override
	protected void initializeImpl() {
		mvcWebApplicationInitializer.initialize();
	}

}
