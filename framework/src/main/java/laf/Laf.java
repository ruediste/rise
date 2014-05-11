package laf;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationModule;

/**
 * This class controls the initialization of the framework.
 */
@Singleton
public class Laf {

	@Inject
	ConfigurationModule configurationModule;

	/**
	 * Initialize the Framework. This will first initialize the configuration
	 * module, load the default configuration, fire the {@link ConfigureEvent}
	 * and finally run the initializers.
	 */
	public void initialize() {

	}

	public boolean isInitialized() {
		return true;
	}

}
