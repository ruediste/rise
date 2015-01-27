package sampleApp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.github.ruediste.laf.component.web.HtmlTemplateFactoriesCP;
import com.github.ruediste.laf.component.web.HtmlTemplateFactoryImpl;
import com.github.ruediste.laf.component.web.defaultConfiguration.ComponentWebDefaultConfiguration;
import com.github.ruediste.laf.core.base.configuration.*;
import com.github.ruediste.laf.core.defaultConfiguration.DefaultConfiguration;
import com.github.ruediste.laf.mvc.web.defaultConfiguration.MvcWebDefaultConfiguration;

import sampleApp.componentTemplates.CPageHtmlTemplate;

@ApplicationScoped
public class SampleAppConfiguration implements ConfigurationDefiner {

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
