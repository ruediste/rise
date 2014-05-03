package laf.configuration;

import java.util.Collection;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationParameterRepository.ParameterEntry;
import laf.initialization.*;

import org.jabsaw.Module;

/**
 * The configuration module is used to configure the framework. Every
 * {@link Singleton} can define {@link ConfigurationParameter}s as public final
 * variables. A parameter has a default value which can be overwritten during
 * application startup.
 *
 *
 * <p>
 * The configuration module has to be {@link #initialize()}d. This will set the
 * {@link ConfigurationParameter#identifier}s. The parameters will have their
 * default values.
 * </p>
 *
 * <p>
 * Afterwards, the default configuration can be loaded by calling
 * {@link #loadDefaultConfiguration()}. This will raise the
 * {@link LoadDefaultConfigurationEvent} which gives other modules the
 * opportunity to initialize the default configuration. The default
 * configuration is different from the default values of the parameters. The
 * default configuration is a configuration which should make the framework
 * usable and can be used as starting point for further customization.
 * </p>
 *
 * <p>
 * It is important to realize that the mechanism of the default configuration is
 * not suitable for the integration of independent modules. Instead, it is
 * designed to load one fixed configuration. To integrate multiple modules, use
 * the initializer system instead.
 * </p>
 *
 * <p>
 * When the {@link CreateInitializersEvent} is raised, all parameters are
 * scanned for {@link Initializer}s using
 * {@link InitializationService#createInitializers(Object)}. This allows easy
 * adaption of the initialization process to the configuration.
 * </p>
 */
@Module(description = "Configuration module integrated with the Initialization module", imported = InitializationModule.class)
@Singleton
public class ConfigurationModule {

	@Inject
	ConfigurationParameterScanner configurationParameterScanner;

	@Inject
	ConfigurationParameterRepository configurationParameterRepository;

	@Inject
	Event<LoadDefaultConfigurationEvent> loadDefaultConfigurationEvent;

	private boolean isInitialized;

	/**
	 * Initialize the configuration. This will set the identifiers of all
	 * {@link ConfigurationParameter}s. Note however, that this will not load
	 * the default configuration (use {@link #loadDefaultConfiguration()}).
	 */
	public void initialize() {
		if (isInitialized) {
			throw new RuntimeException(
					"ConfigurationModule is already initialized");
		}

		// scan for entries
		Collection<ParameterEntry> entries = configurationParameterScanner
				.scan();

		// add entries to repository
		configurationParameterRepository.addEntries(entries);

		// initialize the identifier
		for (ParameterEntry entry : entries) {
			entry.parameter.identifier = entry.instance.getClass().getName()
					+ "." + entry.field.getName();
		}

		isInitialized = true;
	}

	public void loadDefaultConfiguration() {
		checkInitialized();

		loadDefaultConfigurationEvent.fire(new LoadDefaultConfigurationEvent());
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void checkInitialized() {
		if (!isInitialized) {
			throw new RuntimeException("ConfigurationModule is not initialized");
		}
	}
}
