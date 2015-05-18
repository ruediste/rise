package com.github.ruediste.rise.testApp.app;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.integration.DynamicIntegrationModule;
import com.github.ruediste.rise.testApp.component.CPageTemplate;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class TestRestartableApplication extends RestartableApplicationBase {

	@Inject
	ComponentTemplateIndex index;

	@Override
	protected void startImpl(Injector permanentInjector) {
		Salta.createInjector(new DynamicIntegrationModule(permanentInjector))
				.injectMembers(this);
		index.registerTemplate(CPage.class, CPageTemplate.class);
	}

}
