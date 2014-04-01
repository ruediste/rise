package laf;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.initializer.InitializationEngine;
import laf.initializer.Initializer;
import laf.urlMapping.*;
import laf.urlMapping.parameterHandler.IntegerParameterHandler;

/**
 * This class contains all the knots and switches to configure the framework.
 * For all settings, there are default values which should for most cases.
 */
@Singleton
public class LAF {

	@Inject
	InitializationEngine initializationEngine;

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

	/**
	 * Initialize the Framework. This method has to be called after any
	 * configuration changes.
	 */
	public void initialize() {
		ArrayList<Initializer> list = new ArrayList<>();
		list.addAll(initializationEngine
				.createInitializersFromComponents(urlMappingRules));
		list.addAll(initializationEngine
				.createInitializersFromComponents(parameterHandlers));
		initializationEngine.runInitializers(list);
	}

	public Deque<ParameterHandler> getParameterHandlers() {
		return parameterHandlers;
	}

	/**
	 * Configures the framework to default settings
	 */
	@PostConstruct
	void configureDefaults() {
		urlMappingRules.add(new DefaultUrlMappingRule());
		parameterHandlers.add(new IntegerParameterHandler());
	}

}
