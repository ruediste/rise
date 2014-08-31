package sampleApp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import laf.component.web.defaultConfiguration.ComponentWebDefaultConfiguration;
import laf.core.base.configuration.ConfigurationDefiner;
import laf.core.base.configuration.DiscoverConfigruationEvent;
import laf.core.defaultConfiguration.DefaultConfiguration;
import laf.mvc.web.defaultConfiguration.MvcWebDefaultConfiguration;

@ApplicationScoped
public class SampleAppConfiguration implements ConfigurationDefiner {

	@Inject
	DefaultConfiguration defaultConfiguration;
	@Inject
	MvcWebDefaultConfiguration defaultMvcConfiguration;

	@Inject
	ComponentWebDefaultConfiguration defaultComponentConfiguration;

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
