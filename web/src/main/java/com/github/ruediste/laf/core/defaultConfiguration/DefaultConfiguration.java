package com.github.ruediste.laf.core.defaultConfiguration;

import static java.util.stream.Collectors.toList;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializer;
import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializerChain;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.*;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.idSerializers.IntIdSerializer;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.idSerializers.LongIdSerializer;
import com.github.ruediste.laf.core.base.DefaultClassNameMapping;
import com.github.ruediste.laf.core.base.ProjectStage;
import com.github.ruediste.laf.core.base.configuration.ConfigurationDefiner;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParser;
import com.github.ruediste.laf.core.requestParserChain.RequestParserChain;
import com.github.ruediste.laf.core.web.resource.*;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Function;

/**
 * Defines the default configuration of the framework.
 */
public class DefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Injector injector;

	private <T> T get(Class<T> cls) {
		return injector.getInstance(cls);
	}

	public String basePackage = "";

	public Supplier<Function<Class<?>, String>> controllerNameMapping = () -> {
		DefaultClassNameMapping mapping = get(DefaultClassNameMapping.class);
		mapping.initialize(basePackage, "Controller");
		return mapping;
	};

	public Deque<IdentifierSerializer> idSerializers = new LinkedList<>();
	{
		idSerializers.add(get(IntIdSerializer.class));
		idSerializers.add(get(LongIdSerializer.class));
	}

	public Deque<Supplier<ArgumentSerializer>> argumentSerializers = new LinkedList<>();
	{
		argumentSerializers.add(() -> get(IntSerializer.class));
		argumentSerializers.add(() -> get(LongSerializer.class));
		argumentSerializers.add(() -> {
			EntitySerializer entitySerializer = get(EntitySerializer.class);
			entitySerializer.initialize(idSerializers);
			return entitySerializer;
		});
	}

	public ArgumentSerializerChain createArgumentSerializerChain() {
		ArgumentSerializerChain chain = get(ArgumentSerializerChain.class);
		chain.initialize(argumentSerializers.stream().map(Supplier::get)
				.collect(toList()));
		return chain;
	}

	public Deque<RequestParser<HttpRequest>> requestParsers = new LinkedList<>();

	@Inject
	Provider<RequestParserChain<HttpRequest>> requestParserChainProvider;

	public RequestParserChain<HttpRequest> createRequestParserChain() {
		RequestParserChain<HttpRequest> result = requestParserChainProvider
				.get();
		result.add(resourceRequestHandlerCV);
		return result;
	}

	public ProjectStage projectStage;

	public Supplier<ResourceMode> resourceMode = () -> projectStage == ProjectStage.DEVELOPMENT ? ResourceMode.DEVELOPMENT
			: ResourceMode.PRODUCTION;

	public ResourceMode getResourceMode() {
		return resourceMode.get();
	}

	public Supplier<StaticWebResourceRequestHandler> resourceRequestHandler = () -> {

		StaticWebResourceRequestHandler handler = get(StaticWebResourceRequestHandler.class);

		handler.initialize(
				getResourceMode(),
				StreamSupport.stream(
						instance.select(StaticWebResourceBundle.class)
								.spliterator(), false).collect(
						Collectors.toList()));
		return handler;
	};

	public StaticWebResourceRequestHandler createResourceRequestHandler() {
		StaticWebResourceRequestHandler handler = get(StaticWebResourceRequestHandler.class);

		handler.initialize(
				getResourceMode(),
				StreamSupport.stream(
						instance.select(StaticWebResourceBundle.class)
								.spliterator(), false).collect(
						Collectors.toList()));
		return handler;
	}
}
