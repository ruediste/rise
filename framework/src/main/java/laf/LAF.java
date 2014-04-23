package laf;

import javax.inject.Inject;
import javax.inject.Singleton;

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
	FrameworkRootInitializer frameworkRootInitializer;

	private ProjectStage projectStage;

	public ProjectStage getProjectStage() {
		return projectStage;
	}

	public void setProjectStage(ProjectStage projectStage) {
		this.projectStage = projectStage;
	}

	/**
	 * Initialize the Framework. This method has to be called after any
	 * configuration changes.
	 */
	public void initialize() {
		initializationService.initialize(FrameworkRootInitializer.class);
	}

	public boolean isInitialized() {
		return frameworkRootInitializer.isInitialized();
	}

	public enum ProjectStage {
		DEVELOPMENT, PRODUCTION,
	}

}
