package com.github.ruediste.rise.sample.front;

import javax.inject.Inject;

import com.github.ruediste.rise.api.RestartableApplicationModule;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.sample.component.CPageHtmlTemplate;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class App extends RestartableApplicationBase {

	@Inject
	CoreConfiguration config;

	@Inject
	ComponentTemplateIndex componentTemplateIndex;

	@Override
	protected void startImpl(Injector permanentInjector) {
		if (true)
			throw new RuntimeException("boom");
		Salta.createInjector(
				new RestartableApplicationModule(permanentInjector))
				.injectMembers(this);
		config.basePackage = "com.github.ruediste.laf.sample";
		componentTemplateIndex.registerTemplate(CPage.class,
				CPageHtmlTemplate.class);
	}

}
