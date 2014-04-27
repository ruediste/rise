package laf;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationModule;
import laf.initialization.InitializationService;

/**
 * This class contains all the knots and switches to configure the framework.
 * For all settings, there are default values which should for most cases.
 */
@Singleton
public class LAF {

	@Inject
	InitializationService initializationService;

	@Inject
	ConfigurationModule configurationModule;

	@Inject
	FrameworkRootInitializer frameworkRootInitializer;

	@Inject
	Event<ConfigureEvent> configureEvent;

	private ProjectStage projectStage;

	public ProjectStage getProjectStage() {
		return projectStage;
	}

	public void setProjectStage(ProjectStage projectStage) {
		this.projectStage = projectStage;
	}

	/**
	 * Initialize the Framework. This will first initialize the configuration
	 * module, load the default configuration, fire the {@link ConfigureEvent}
	 * and finally run the initializers.
	 */
	public void initialize() {
		configurationModule.initialize();
		configurationModule.loadDefaultConfiguration();
		configureEvent.fire(new ConfigureEvent());
		initializationService.initialize(FrameworkRootInitializer.class);
	}

	public boolean isInitialized() {
		return frameworkRootInitializer.isInitialized();
	}

	public enum ProjectStage {
		DEVELOPMENT, PRODUCTION,
	}

}
