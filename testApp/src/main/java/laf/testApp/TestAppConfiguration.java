package laf.testApp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.web.HtmlTemplateFactoriesCP;
import laf.component.web.HtmlTemplateFactoryImpl;
import laf.component.web.defaultConfiguration.ComponentWebDefaultConfiguration;
import laf.core.base.configuration.*;
import laf.core.defaultConfiguration.DefaultConfiguration;
import laf.mvc.web.defaultConfiguration.MvcWebDefaultConfiguration;
import laf.testApp.componentTemplates.CPageHtmlTemplate;

@ApplicationScoped
public class TestAppConfiguration implements ConfigurationDefiner {

	@Inject
	DefaultConfiguration defaultConfiguration;
	@Inject
	MvcWebDefaultConfiguration defaultMvcConfiguration;

	@Inject
	ComponentWebDefaultConfiguration defaultComponentConfiguration;

	@Inject
	Instance<Object> instance;

	private <T> T get(Class<T> cls) {
		return instance.select(cls).get();
	}

	@ExtendConfiguration
	public void produce(HtmlTemplateFactoriesCP val) {
		HtmlTemplateFactoryImpl factory = get(HtmlTemplateFactoryImpl.class);
		factory.addTemplatesFromPackage(CPageHtmlTemplate.class.getPackage());
		val.get().add(factory);
	}

	protected void registerConfigurationValueProviders(
			@Observes DiscoverConfigruationEvent e) {
		e.add(defaultConfiguration);
		e.add(defaultMvcConfiguration);
		e.add(defaultComponentConfiguration);
		e.add(this);
		e.addPropretiesFile("configuration.properties");
		e.lock();
	}

}
