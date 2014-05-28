package laf.defaultConfiguration;

import java.util.ArrayList;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.configuration.ConfigurationDefiner;
import laf.httpRequestMapping.HttpRequestMappingRules;
import laf.httpRequestMapping.defaultRule.BasePackage;
import laf.httpRequestMapping.defaultRule.DefaultHttpRequestMappingRuleFactory;
import laf.httpRequestMapping.parameterHandler.ParameterHandler;
import laf.httpRequestMapping.parameterHandler.ParameterHandlers;
import laf.httpRequestProcessing.HttpRequestProcessorConfigurationParameter;
import laf.httpRequestProcessing.defaultProcessor.*;
import laf.requestProcessing.ControllerInvokerConfigurationParameter;
import laf.requestProcessing.ParameterLoaderConfigurationParameter;
import laf.requestProcessing.RequestProcessorConfigurationParameter;
import laf.requestProcessing.defaultProcessor.DefaultControllerInvoker;
import laf.requestProcessing.defaultProcessor.DefaultParameterLoader;
import laf.requestProcessing.defaultProcessor.DefaultRequestProcessor;

import com.google.common.collect.Iterators;

/**
 * Defines the default configuration of the framework.
 */
public class DefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	public void produce(HttpRequestMappingRules rules) {
		rules.set(instance.select(DefaultHttpRequestMappingRuleFactory.class)
				.get().createRules());
	}

	public void produce(BasePackage basePackage) {
		basePackage.set(null);
	}

	public void produce(ParameterHandlers parameterHandlers) {
		ArrayList<ParameterHandler> handlers = new ArrayList<>();
		Iterators.addAll(handlers, instance.select(ParameterHandler.class)
				.iterator());
		parameterHandlers.set(handlers);
	}

	public void produce(RequestProcessorConfigurationParameter val) {
		val.set(instance.select(DefaultRequestProcessor.class).get());
	}

	public void produce(ControllerInvokerConfigurationParameter val) {
		val.set(instance.select(DefaultControllerInvoker.class).get());
	}

	public void produce(ParameterLoaderConfigurationParameter val) {
		val.set(instance.select(DefaultParameterLoader.class).get());
	}

	public void produce(HttpRequestProcessorConfigurationParameter val) {
		val.set(instance.select(DefaultHttpRequestProcessor.class).get());
	}

	public void produce(RequestParserConfigurationParameter val) {
		val.set(instance.select(DefaultRequestParser.class).get());
	}

	public void produce(ResultRendererConfigurationParameter val) {
		val.set(instance.select(DefaultResultRenderer.class).get());
	}

}
