package laf.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.ruediste.laf.component.web.defaultConfiguration.ComponentWebDefaultConfiguration;
import com.github.ruediste.laf.core.base.configuration.DiscoverConfigruationEvent;
import com.github.ruediste.laf.core.defaultConfiguration.DefaultConfiguration;
import com.github.ruediste.laf.mvc.web.defaultConfiguration.MvcWebDefaultConfiguration;

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
