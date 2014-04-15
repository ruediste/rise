package laf;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.controllerInfo.impl.ControllerInfoRepositoryInitializer;
import laf.urlMapping.DefaultUrlMappingRule;
import laf.urlMapping.ParameterHandlerInitializer;
import laf.urlMapping.parameterHandler.IntegerParameterHandler;

public class DefaultLafConfigurator {

	@Inject
	LAF laf;

	@Inject
	Instance<Object> instance;

	/**
	 * Configures the framework to default settings
	 */
	public void configure() {
		laf.getUrlMappingRules().add(
				instance.select(DefaultUrlMappingRule.class).get());
		laf.getParameterHandlers().add(new IntegerParameterHandler());
		laf.getAdditionalComponents().add(
				instance.select(ControllerInfoRepositoryInitializer.class)
				.get());
		laf.getAdditionalComponents().add(
				instance.select(ParameterHandlerInitializer.class).get());
	}

}
