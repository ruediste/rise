package com.github.ruediste.rise.test;

import org.junit.Before;

import com.github.ruediste.rise.api.DynamicApplicationModule;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CorePermanentModule;
import com.github.ruediste.rise.core.front.ApplicationEventQueue;
import com.github.ruediste.rise.core.front.LoggerModule;
import com.github.ruediste.rise.mvc.web.MvcWebPermanentModule;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public abstract class SaltaTestBase {

	private Injector permanentInjector;

	@Before
	public void beforeSaltaTest() throws Exception {
		permanentInjector = Salta.createInjector(new MvcWebPermanentModule(),
				new CorePermanentModule(null), new LoggerModule());
		permanentInjector.getInstance(ApplicationEventQueue.class)
				.submit(this::startInAET).get();
	}

	protected void initialize() {
	}

	private void startInAET() {
		initialize();
		InitializerUtil.runInitializers(permanentInjector);

		Injector instanceInjector = Salta
				.createInjector(new DynamicApplicationModule(permanentInjector));

		instanceInjector.getInstance(CoreConfiguration.class).dynamicClassLoader = Thread
				.currentThread().getContextClassLoader();

		InitializerUtil.runInitializers(instanceInjector);
		instanceInjector.injectMembers(this);
	}
}
