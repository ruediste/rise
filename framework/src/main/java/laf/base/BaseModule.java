package laf.base;

import javax.inject.Singleton;

import org.jabsaw.Module;

@Module(description = "Base classes of the LAF Framework")
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
