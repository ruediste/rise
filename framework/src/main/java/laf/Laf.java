package laf;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationModule;
import laf.initialization.InitializationService;
import laf.initialization.laf.FrameworkRootInitializer;
import laf.initialization.laf.LafConfigurationPhase;
import laf.initialization.laf.LafInitializationPhase;

/**
 * This class controls the initialization of the framework.
 */
@Singleton
public class Laf {

	@Inject
	InitializationService initializationService;

	@Inject
	ConfigurationModule configurationModule;

	@Inject
	FrameworkRootInitializer frameworkRootInitializer;

	/**
	 * Initialize the Framework. This will first initialize the configuration
	 * module, load the default configuration, fire the {@link ConfigureEvent}
	 * and finally run the initializers.
	 */
	public void initialize() {
		initializationService.initialize(LafConfigurationPhase.class,
				FrameworkRootInitializer.class);
		initializationService.initialize(LafInitializationPhase.class,
				FrameworkRootInitializer.class);
	}

	public boolean isInitialized() {
		return frameworkRootInitializer.isInitialized();
	}

}
