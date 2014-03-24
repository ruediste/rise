package laf;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import laf.urlMapping.ParameterHandler;
import laf.urlMapping.UrlMappingRule;

/**
 * This class contains all the knots and switches to configure the framework.
 * For all settings, there are default values which should for most cases.
 */
@Singleton
public class LAF {

	final private ArrayDeque<UrlMappingRule> urlMappingRules = new ArrayDeque<>();

	public ArrayDeque<UrlMappingRule> getUrlMappingRules() {
		return urlMappingRules;
	}

	public ProjectStage getProjectStage() {
		return projectStage;
	}

	public void setProjectStage(ProjectStage projectStage) {
		this.projectStage = projectStage;
	}

	public enum ProjectStage {
		DEVELOPMENT, PRODUCTION,
	}

	private ProjectStage projectStage;

	private final Deque<ParameterHandler> parameterHandlers = new ArrayDeque<>();
	private final Deque<Runnable> initializers = new ArrayDeque<>();

	/**
	 * Initialize the Framework. This method has to be called after any
	 * configuration changes.
	 */
	public void initialize() {

	}

	public Deque<ParameterHandler> getParameterHandlers() {
		return parameterHandlers;
	}

	public Deque<Runnable> getInitializers() {
		return initializers;
	}

	/**
	 * Configures the framework to default settings
	 */
	@PostConstruct
	void configureDefaults() {

	}

}
