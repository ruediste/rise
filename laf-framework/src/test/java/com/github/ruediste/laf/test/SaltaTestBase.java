package com.github.ruediste.laf.test;

import org.junit.Before;

import com.github.ruediste.laf.api.DynamicApplicationModule;
import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.CorePermanentModule;
import com.github.ruediste.laf.core.front.ApplicationEventQueue;
import com.github.ruediste.laf.core.front.LoggerModule;
import com.github.ruediste.laf.mvc.web.MvcWebPermanentModule;
import com.github.ruediste.laf.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public abstract class SaltaTestBase {

	private Injector permanentInjector;

	@Before
	public void beforeSaltaTest() throws Exception {
		permanentInjector = Salta.createInjector(new MvcWebPermanentModule(),
				new CorePermanentModule(), new LoggerModule());
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
