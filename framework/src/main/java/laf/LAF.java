package laf;

import java.util.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.initialization.InitializationService;
import laf.initialization.Initializer;
import laf.urlMapping.ParameterHandler;
import laf.urlMapping.UrlMappingRule;

/**
 * This class contains all the knots and switches to configure the framework.
 * For all settings, there are default values which should for most cases.
 */
@Singleton
public class LAF {

	@Inject
	InitializationService initializationEngine;

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

	private boolean initialized;

	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Initialize the Framework. This method has to be called after any
	 * configuration changes.
	 */
	public void initialize() {
		ArrayList<Initializer> list = new ArrayList<>();
		list.addAll(initializationEngine
				.createInitializers(urlMappingRules));
		list.addAll(initializationEngine
				.createInitializers(parameterHandlers));
		list.addAll(initializationEngine
				.createInitializers(additionalComponents));
		// initializationEngine.runInitializers(list);
		initialized = true;
	}

	public Deque<ParameterHandler> getParameterHandlers() {
		return parameterHandlers;
	}

	public ArrayList<Object> getAdditionalComponents() {
		return additionalComponents;
	}

	public void setAdditionalComponents(ArrayList<Object> additionalComponents) {
		this.additionalComponents = additionalComponents;
	}

	private ArrayList<Object> additionalComponents = new ArrayList<>();
}
