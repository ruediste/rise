package com.github.ruediste.laf.mvc.web.defaultConfiguration;

import javax.inject.Inject;

import com.github.ruediste.laf.core.base.configuration.ConfigurationDefiner;
import com.github.ruediste.laf.core.defaultConfiguration.*;
import com.github.ruediste.laf.mvc.core.*;
import com.github.ruediste.laf.mvc.web.*;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcWebDefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Injector injector;

	private <T> T get(Class<T> cls) {
		return injector.getInstance(cls);
	}

	public void produce(HttpRequestMapperCP val,
			ControllerNameMappingCP nameMapping) {
		HttpRequestMapperImpl mapper = instance.select(
				HttpRequestMapperImpl.class).get();
		mapper.initialize(nameMapping.get());
		val.set(mapper);
	}

	public void produce(PersistenceRequestHandlerCP val) {
		val.set(instance.select(PersistenceRequestHandler.class).get());
	}

	public void produce(ArgumentLoadingRequestHandlerCP val,
			ArgumentSerializerChainCP serializerChain) {
		ArgumentLoadingRequestHandler handler = instance.select(
				ArgumentLoadingRequestHandler.class).get();
		handler.initialize(serializerChain.get());
		val.set(handler);
	}

	public void produce(ControllerInvokerCP val) {
		val.set(instance.select(ControllerInvoker.class).get());
	}

	public void produce(RequestMappingUtilInitializerCP val,
			HttpRequestMapperCP requestMapperCP,
			ArgumentSerializerChainCP serializerChainCP) {
		RequestMappingUtilInitializer result = instance.select(
				RequestMappingUtilInitializer.class).get();

		result.initialize(requestMapperCP.get(), serializerChainCP.get());
		val.set(result);
	}

	public void produce(RenderResultRendererCP val) {
		val.set(instance.select(ResultRenderer.class).get());
	}

	public void produce(RequestHandlerCP val,
			RequestMappingUtilInitializerCP requestMappingUtilInitializerCV,
			RenderResultRendererCP resultRendererCV,
			PersistenceRequestHandlerCP persistenceCV,
			ArgumentLoadingRequestHandlerCP argumentLoaderCV,
			ControllerInvokerCP invokerCV) {
		requestMappingUtilInitializerCV.get().setDelegate(
				resultRendererCV.get());
		resultRendererCV.get().setDelegate(persistenceCV.get());
		persistenceCV.get().setDelegate(argumentLoaderCV.get());
		argumentLoaderCV.get().setDelegate(invokerCV.get());
		val.set(requestMappingUtilInitializerCV.get());
	}

	public void produce(RequestParserCP val, HttpRequestMapperCP mapper,
			RequestHandlerCP handler) {
		MvcWebRequestParser result = instance.select(MvcWebRequestParser.class)
				.get();
		result.initialize(mapper.get(), handler.get());
		val.set(result);
	}

	@ExtendConfiguration
	public void produce(HttpRequestParserChainCP val, RequestParserCP parser) {
		val.get().parsers.addLast(parser.get());
	}
}
