package com.github.ruediste.laf.core.defaultConfiguration;

import static java.util.stream.Collectors.toList;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.ClassReader;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializer;
import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializerChain;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.*;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.idSerializers.IntIdSerializer;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.idSerializers.LongIdSerializer;
import com.github.ruediste.laf.core.base.DefaultClassNameMapping;
import com.github.ruediste.laf.core.base.ProjectStage;
import com.github.ruediste.laf.core.base.configuration.ConfigurationDefiner;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.*;
import com.github.ruediste.laf.core.web.resource.ResourceMode;
import com.github.ruediste.laf.core.web.resource.StaticWebResourceRequestHandler;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Function;

/**
 * Defines the default configuration of the framework.
 */
@Singleton
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

	@PostConstruct
	private void setupIdSerializers() {
		idSerializers.add(get(IntIdSerializer.class));
		idSerializers.add(get(LongIdSerializer.class));
	}

	public Deque<Supplier<ArgumentSerializer>> argumentSerializers = new LinkedList<>();

	@PostConstruct
	private void setupArgumentSerializers() {
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

	/**
	 * When handling a request, the request parsers are evaluated until the
	 * first one returns a non-null result.
	 */
	public final Deque<RequestParser> requestParsers = new LinkedList<>();

	/**
	 * This is the request parser using the {@link #PathInfoIndex} to parse a
	 * request. initially added to {@link #requestParsers}
	 */
	public RequestParser pathInfoIndexRequestParser;

	@PostConstruct
	private void setupRequestParsers(PathInfoIndex pathInfoIndex) {
		pathInfoIndexRequestParser = request -> {
			RequestParser parser = pathInfoIndex.getHandler(request
					.getPathInfo());
			if (parser != null) {
				return parser.parse(request);
			}
			return null;
		};
		requestParsers.add(pathInfoIndexRequestParser);
	}

	public RequestParseResult parse(HttpRequest request) {
		for (RequestParser parser : requestParsers) {
			RequestParseResult result = parser.parse(request);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public ProjectStage projectStage;

	public Supplier<ResourceMode> resourceMode = () -> projectStage == ProjectStage.DEVELOPMENT ? ResourceMode.DEVELOPMENT
			: ResourceMode.PRODUCTION;

	public ResourceMode getResourceMode() {
		return resourceMode.get();
	}

	public Supplier<StaticWebResourceRequestHandler> resourceRequestHandler = () -> {

		StaticWebResourceRequestHandler handler = get(StaticWebResourceRequestHandler.class);
		// TODO: collect bundles
		handler.initialize(getResourceMode());
		return handler;
	};

	public StaticWebResourceRequestHandler createResourceRequestHandler() {
		return resourceRequestHandler.get();
	}

	/**
	 * Flags to be used when calling
	 * {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, int)} for class
	 * change notification.
	 */
	public int classScanningFlags = ClassReader.SKIP_CODE
			+ ClassReader.SKIP_DEBUG;

	public long fileChangeSettleDelayMs = 10;
}
