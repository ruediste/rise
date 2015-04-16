package com.github.ruediste.laf.core;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Function;

/**
 * Defines the default configuration of the framework.
 */
@Singleton
public class CoreConfiguration {

	@Inject
	Injector injector;

	private <T> T get(Class<T> cls) {
		return injector.getInstance(cls);
	}

	public String basePackage = "";
	public String controllerSuffix = "Controller";

	public Supplier<Function<ClassNode, String>> controllerNameMapperSupplier = () -> {
		DefaultClassNameMapping mapping = get(DefaultClassNameMapping.class);
		mapping.initialize(basePackage, controllerSuffix);
		return mapping;
	};

	private Function<ClassNode, String> controllerNameMapper;

	public String calculateControllerName(ClassNode node) {
		return controllerNameMapper.apply(node);
	}

	public void initialize() {
		controllerNameMapper = controllerNameMapperSupplier.get();
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

	/**
	 * Flags to be used when calling
	 * {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, int)} for class
	 * change notification.
	 */
	public int classScanningFlags = ClassReader.SKIP_CODE
			+ ClassReader.SKIP_DEBUG;

	public long fileChangeSettleDelayMs = 10;
}
