package laf.configuration;

import java.util.Collection;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationParameterRepository.ParameterEntry;
import laf.initialization.CreateInitializersEvent;
import laf.initialization.InitializationModule;
import laf.initialization.InitializationService;
import laf.initialization.Initializer;

import org.jabsaw.Module;

/**
 * The configuration module is used to configure the framework. Every
 * {@link Singleton} can define {@link ConfigurationParameter}s as public final
 * variables. A parameter has a default value which can be overwritten during
 * application startup.
 *
 * <p>
 * The configuration module has to be {@link #initialize()}d. This will set the
 * {@link ConfigurationParameter#identifier}s. Afterwards, the default
 * configuration can be loaded by calling {@link #loadDefaultConfiguration()}.
 * This will raise the {@link LoadDefaultConfigurationEvent} which gives other
 * modules the opportunity to initialize the default configuration. For special
 * circumstances there also exists the {@link LoadDefaultConfigurationPreEvent}
 * and the {@link LoadDefaultConfigurationPostEvent}, which are raised before
 * respectively after the main event.
 * </p>
 *
 * <p>
 * When the {@link CreateInitializersEvent} is raised, all parameters are
 * scanned for {@link Initializer}s using
 * {@link InitializationService#createInitializers(Object)}. This allows easy
 * adaption of the initialization process to the configuration.
 * </p>
 */
@Module(imported = InitializationModule.class)
@Singleton
public class ConfigurationModule {

	@Inject
	ConfigurationParameterScanner configurationParameterScanner;

	@Inject
	ConfigurationParameterRepository configurationParameterRepository;

	@Inject
	Event<LoadDefaultConfigurationPreEvent> loadDefaultConfigurationPreEvent;

	@Inject
	Event<LoadDefaultConfigurationEvent> loadDefaultConfigurationEvent;

	@Inject
	Event<LoadDefaultConfigurationPostEvent> loadDefaultConfigurationPostEvent;

	private boolean isInitialized;

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

		loadDefaultConfigurationPreEvent
		.fire(new LoadDefaultConfigurationPreEvent());
		loadDefaultConfigurationEvent.fire(new LoadDefaultConfigurationEvent());
		loadDefaultConfigurationPostEvent
		.fire(new LoadDefaultConfigurationPostEvent());
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
