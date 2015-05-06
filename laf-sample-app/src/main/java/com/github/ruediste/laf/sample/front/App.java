package com.github.ruediste.laf.sample.front;

import javax.inject.Inject;

import com.github.ruediste.laf.api.DynamicApplicationModule;
import com.github.ruediste.laf.component.TemplateIndex;
import com.github.ruediste.laf.component.web.components.CPage;
import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.front.DynamicApplicationBase;
import com.github.ruediste.laf.sample.component.CPageHtmlTemplate;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class App extends DynamicApplicationBase {

	@Inject
	CoreConfiguration config;

	@Inject
	TemplateIndex templateIndex;

	@Override
	protected void startImpl(Injector permanentInjector) {
		Salta.createInjector(new DynamicApplicationModule(permanentInjector))
				.injectMembers(this);
		config.basePackage = "com.github.ruediste.laf.sample";
		templateIndex.registerTemplate(CPage.class, CPageHtmlTemplate.class);
	}

}
