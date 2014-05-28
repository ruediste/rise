package laf.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import laf.configuration.DiscoverConfigruationEvent;
import laf.defaultConfiguration.DefaultConfiguration;

@ApplicationScoped
public class TestConfigurationFactory {

	@Inject
	DefaultConfiguration defaultConfiguration;

	protected void registerConfigurationValueProviders(
			@Observes DiscoverConfigruationEvent e) {
		e.add(defaultConfiguration);
	}

}
