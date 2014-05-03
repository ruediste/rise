package laf;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.base.ConfigureEvent;
import laf.configuration.ConfigurationModule;
import laf.initialization.*;

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

	@Inject
	Event<ConfigureEvent> configureEvent;

	/**
	 * Initialize the Framework. This will first initialize the configuration
	 * module, load the default configuration, fire the {@link ConfigureEvent}
	 * and finally run the initializers.
	 */
	public void initialize() {
		configurationModule.initialize();
		configurationModule.loadDefaultConfiguration();
		configureEvent.fire(new ConfigureEvent());
		initializationService.initialize(DefaultPhase.class,
				FrameworkRootInitializer.class);
	}

	public boolean isInitialized() {
		return frameworkRootInitializer.isInitialized();
	}

}
