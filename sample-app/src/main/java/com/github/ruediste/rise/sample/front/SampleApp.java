package com.github.ruediste.rise.sample.front;

import javax.inject.Inject;

import com.github.ruediste.rise.api.RestartableApplicationModule;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.DefaultRequestErrorHandler;
import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.sample.SamplePackage;
import com.github.ruediste.rise.sample.component.CPageHtmlTemplate;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class SampleApp extends RestartableApplicationBase {

	@Inject
	CoreConfiguration config;

	@Inject
	ComponentTemplateIndex componentTemplateIndex;

	@Inject
	DefaultRequestErrorHandler errorHandler;

	@Override
	protected void startImpl(Injector permanentInjector) {
		Salta.createInjector(
				new RestartableApplicationModule(permanentInjector))
				.injectMembers(this);

		errorHandler.initialize(util -> util.go(ReqestErrorController.class)
				.index());
		config.requestErrorHandler = errorHandler;

		config.setBasePackage(SamplePackage.class);

		componentTemplateIndex.registerTemplate(CPage.class,
				CPageHtmlTemplate.class);
	}

}
