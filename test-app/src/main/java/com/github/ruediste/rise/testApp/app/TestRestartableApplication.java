package com.github.ruediste.rise.testApp.app;

import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.integration.DynamicIntegrationModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class TestRestartableApplication extends RestartableApplicationBase {

	@Override
	protected void startImpl(Injector permanentInjector) {
		Salta.createInjector(new DynamicIntegrationModule(permanentInjector))
				.injectMembers(this);
	}

}
