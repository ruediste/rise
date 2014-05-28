package laf.base;

import javax.inject.Singleton;

import laf.attachedProperties.AttachedPropertiesModule;
import laf.configuration.ConfigurationModule;

import org.jabsaw.Module;

@Module(description = "Meta Module of the Base classes of the LAF Framework", exported = {
		AttachedPropertiesModule.class, ConfigurationModule.class,
		BaseModuleImpl.class }, hideFromDependencyGraphOutput = true, includePackage = false)
@Singleton
public class BaseModule {

	public enum ProjectStage {
		DEVELOPMENT, PRODUCTION,
	}

	private ProjectStage projectStage;

	public ProjectStage getProjectStage() {
		return projectStage;
	}

	public void setProjectStage(ProjectStage projectStage) {
		this.projectStage = projectStage;
	}
}
