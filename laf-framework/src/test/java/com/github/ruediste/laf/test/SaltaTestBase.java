package com.github.ruediste.laf.test;

import org.junit.Before;

import com.github.ruediste.laf.core.CoreApplicationInstanceModule;
import com.github.ruediste.laf.core.CoreApplicationModule;
import com.github.ruediste.laf.core.front.ApplicationEventQueue;
import com.github.ruediste.laf.core.front.LoggerModule;
import com.github.ruediste.laf.mvc.web.MvcWebDynamicModule;
import com.github.ruediste.laf.mvc.web.MvcWebPermanentModule;
import com.github.ruediste.laf.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public abstract class SaltaTestBase {

	private Injector applicationInjector;

	@Before
	public void beforeSaltaTest() throws Exception {
		applicationInjector = Salta.createInjector(
				new MvcWebPermanentModule(), new CoreApplicationModule(),
				new LoggerModule());
		applicationInjector.getInstance(ApplicationEventQueue.class)
				.submit(this::startInAET).get();
	}

	protected void initialize() {
	}

	private void startInAET() {
		initialize();
		InitializerUtil.runInitializers(applicationInjector);

		Injector instanceInjector = Salta.createInjector(
				new MvcWebDynamicModule(applicationInjector),
				new CoreApplicationInstanceModule(), new LoggerModule());
		InitializerUtil.runInitializers(instanceInjector);
		instanceInjector.injectMembers(this);
	}
}
