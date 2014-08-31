package laf.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import laf.component.web.defaultConfiguration.ComponentWebDefaultConfiguration;
import laf.core.base.configuration.DiscoverConfigruationEvent;
import laf.core.defaultConfiguration.DefaultConfiguration;
import laf.mvc.web.defaultConfiguration.MvcWebDefaultConfiguration;

@ApplicationScoped
public class TestConfigurationFactory {

	@Inject
	DefaultConfiguration defaultConfiguration;

	@Inject
	MvcWebDefaultConfiguration mvcWebDefaultConfiguration;

	@Inject
	ComponentWebDefaultConfiguration componentWebDefaultConfiguration;

	protected void registerConfigurationValueProviders(
			@Observes DiscoverConfigruationEvent e) {
		e.add(defaultConfiguration);
		e.add(mvcWebDefaultConfiguration);
		e.add(componentWebDefaultConfiguration);
	}

}
