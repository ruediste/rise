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
import laf.httpRequestProcessing.HttpRequestProcessorConfigurationValue;
import laf.httpRequestProcessing.RequestParserConfigurationValue;
import laf.httpRequestProcessing.ResultRendererConfigurationValue;
import laf.httpRequestProcessing.defaultProcessor.DefaultHttpRequestProcessor;
import laf.httpRequestProcessing.defaultProcessor.DefaultRequestParser;
import laf.httpRequestProcessing.defaultProcessor.DefaultResultRenderer;
import laf.requestProcessing.ControllerInvokerConfigurationValue;
import laf.requestProcessing.ParameterLoaderConfigurationValue;
import laf.requestProcessing.RequestProcessorConfigurationValue;
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

	public void produce(RequestProcessorConfigurationValue val) {
		val.set(instance.select(DefaultRequestProcessor.class).get());
	}

	public void produce(ControllerInvokerConfigurationValue val) {
		val.set(instance.select(DefaultControllerInvoker.class).get());
	}

	public void produce(ParameterLoaderConfigurationValue val) {
		val.set(instance.select(DefaultParameterLoader.class).get());
	}

	public void produce(HttpRequestProcessorConfigurationValue val) {
		val.set(instance.select(DefaultHttpRequestProcessor.class).get());
	}

	public void produce(RequestParserConfigurationValue val) {
		val.set(instance.select(DefaultRequestParser.class).get());
	}

	public void produce(ResultRendererConfigurationValue val) {
		val.set(instance.select(DefaultResultRenderer.class).get());
	}

}
