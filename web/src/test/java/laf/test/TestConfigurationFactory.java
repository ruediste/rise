package laf.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import laf.core.base.configuration.DiscoverConfigruationEvent;
import laf.core.defaultConfiguration.DefaultConfiguration;
import laf.mvc.configuration.MvcDefaultConfiguration;

@ApplicationScoped
public class TestConfigurationFactory {

	@Inject
	DefaultConfiguration defaultConfiguration;

	@Inject
	MvcDefaultConfiguration mvcDefaultConfiguration;

	protected void registerConfigurationValueProviders(
			@Observes DiscoverConfigruationEvent e) {
		e.add(defaultConfiguration);
		e.add(mvcDefaultConfiguration);
	}

}
