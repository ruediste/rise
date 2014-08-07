package laf.core.defaultConfiguration;

import java.util.*;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.configuration.ConfigurationDefiner;
import laf.core.controllerInfo.*;
import laf.core.http.ContentType;
import laf.core.http.requestMapping.HttpRequestMappingRules;
import laf.core.http.requestMapping.defaultRule.BasePackage;
import laf.core.http.requestMapping.defaultRule.DefaultHttpRequestMappingRuleFactory;
import laf.core.http.requestMapping.parameterHandler.ParameterHandler;
import laf.core.http.requestMapping.parameterHandler.ParameterHandlers;
import laf.core.http.requestProcessing.HttpRequestProcessorConfigurationParameter;
import laf.core.http.requestProcessing.defaultProcessor.*;
import laf.core.requestProcessing.*;

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

	public void produce(ControllerTypeRequestProcessorsCP map) {
		map.set(new HashMap<Class<? extends ControllerType>, RequestProcessor>());
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
		// type.set("text/html");
	}

	public void produce(ControllerDiscoverersCP val) {
		val.set(new ArrayDeque<ControllerDiscoverer>());
	}
}
