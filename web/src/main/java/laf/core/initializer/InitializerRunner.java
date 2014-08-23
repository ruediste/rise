package laf.core.initializer;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import laf.base.configuration.ConfigurationValue;

@Startup
@ApplicationScoped
public class InitializerRunner {

	@Inject
	ConfigurationValue<InitializerCP> initializersCV;

	@PostConstruct
	public void initialize() {
		for (Runnable initializer : initializersCV.value().get()) {
			initializer.run();
		}
	}
}
