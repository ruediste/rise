package laf.defaultConfiguration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.configuration.ConfigurationDefiner;
import laf.controllerInfo.ControllerDiscoverer;
import laf.controllerInfo.ControllerDiscoverers;
import laf.http.ContentType;
import laf.http.requestMapping.HttpRequestMappingRules;
import laf.http.requestMapping.defaultRule.BasePackage;
import laf.http.requestMapping.defaultRule.DefaultHttpRequestMappingRuleFactory;
import laf.http.requestMapping.parameterHandler.ParameterHandler;
import laf.http.requestMapping.parameterHandler.ParameterHandlers;
import laf.http.requestProcessing.HttpRequestProcessorConfigurationParameter;
import laf.http.requestProcessing.defaultProcessor.DefaultHttpRequestProcessor;
import laf.http.requestProcessing.defaultProcessor.DefaultRequestParser;
import laf.http.requestProcessing.defaultProcessor.HttpRenderResultRenderer;
import laf.http.requestProcessing.defaultProcessor.RequestParserConfigurationParameter;
import laf.http.requestProcessing.defaultProcessor.ResultRenderer;
import laf.http.requestProcessing.defaultProcessor.ResultRenderers;
import laf.mvc.MvcControllerDiscoverer;
import laf.requestProcessing.ControllerTypeRequestProcessors;
import laf.requestProcessing.ErrorHandlingRequestProcessor;
import laf.requestProcessing.RequestProcessor;
import laf.requestProcessing.RequestProcessorConfigurationParameter;
import laf.requestProcessing.SwitchControllerTypeRequestProcessor;

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
		SwitchControllerTypeRequestProcessor switchProcessor = instance.select(
				SwitchControllerTypeRequestProcessor.class).get();
		ErrorHandlingRequestProcessor errorProcessor = instance.select(
				ErrorHandlingRequestProcessor.class).get();
		errorProcessor.initialize(switchProcessor);

		val.set(errorProcessor);
	}

	public void produce(ControllerTypeRequestProcessors map) {
		map.set(new HashMap<Object, RequestProcessor>());
	}

	public void produce(HttpRequestProcessorConfigurationParameter val) {
		val.set(instance.select(DefaultHttpRequestProcessor.class).get());
	}

	public void produce(RequestParserConfigurationParameter val) {
		val.set(instance.select(DefaultRequestParser.class).get());
	}

	public void produce(ResultRenderers val) {
		ArrayDeque<ResultRenderer> renderers = new ArrayDeque<>();
		renderers.add(instance.select(HttpRenderResultRenderer.class).get());
		val.set(renderers);
	}

	public void produce(ContentType type) {
		type.set("application/xhtml+xml");
	}

	public void produce(ControllerDiscoverers val) {
		ArrayDeque<ControllerDiscoverer> discoverers = new ArrayDeque<>();
		discoverers.add(instance.select(MvcControllerDiscoverer.class).get());
		val.set(discoverers);
	}
}
