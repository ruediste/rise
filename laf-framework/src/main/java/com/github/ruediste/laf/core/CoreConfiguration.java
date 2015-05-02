package com.github.ruediste.laf.core;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.AnnotatedType;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializer;
import com.github.ruediste.laf.core.argumentSerializer.EntityArgumentSerializer;
import com.github.ruediste.laf.core.argumentSerializer.IntSerializer;
import com.github.ruediste.laf.core.argumentSerializer.LongSerializer;
import com.github.ruediste.laf.core.argumentSerializer.StringSerializer;
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

	/**
	 * Supplier create a name mapper. The name mapper is used to map a class to
	 * it's name
	 */
	public Supplier<Function<ClassNode, String>> controllerNameMapperSupplier = () -> {
		DefaultClassNameMapping mapping = get(DefaultClassNameMapping.class);
		mapping.initialize(basePackage, controllerSuffix);
		return mapping;
	};

	private Function<ClassNode, String> controllerNameMapper;

	public String calculateControllerName(ClassNode node) {
		return controllerNameMapper.apply(node);
	}

	/**
	 * Called after the configuration phase is completed
	 */
	public void initialize() {
		controllerNameMapper = controllerNameMapperSupplier.get();
		argumentSerializers = argumentSerializerSuppliers.stream()
				.map(Supplier::get).collect(toList());
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

	/**
	 * The classloader used to load the classes in the dynamic class space
	 */
	public ClassLoader dynamicClassLoader;

	public final LinkedList<Supplier<ArgumentSerializer>> argumentSerializerSuppliers = new LinkedList<>();

	public class SerializerSupplierRefs {
		Supplier<ArgumentSerializer> longSerializerSupplier = () -> injector
				.getInstance(LongSerializer.class);
		Supplier<ArgumentSerializer> intSerializerSupplier = () -> injector
				.getInstance(IntSerializer.class);
		Supplier<ArgumentSerializer> stringSerializerSupplier = () -> injector
				.getInstance(StringSerializer.class);

		Supplier<ArgumentSerializer> entitySerializerSupplier = () -> injector
				.getInstance(EntityArgumentSerializer.class);

	}

	public final SerializerSupplierRefs serializerSupplierRefs = new SerializerSupplierRefs();

	{
		argumentSerializerSuppliers
				.add(serializerSupplierRefs.longSerializerSupplier);
		argumentSerializerSuppliers
				.add(serializerSupplierRefs.intSerializerSupplier);
		argumentSerializerSuppliers
				.add(serializerSupplierRefs.stringSerializerSupplier);
		argumentSerializerSuppliers
				.add(serializerSupplierRefs.entitySerializerSupplier);
	}

	private java.util.List<ArgumentSerializer> argumentSerializers;

	public String generateArgument(AnnotatedType type, Object value) {
		return argumentSerializers
				.stream()
				.map(a -> a.generate(type, value))
				.filter(x -> x != null)
				.findFirst()
				.orElseThrow(
						() -> new RuntimeException(
								"No argument serializer found for "
										+ type.getType()));
	}

	public Supplier<Object> parseArgument(AnnotatedType type, String urlPart) {
		return argumentSerializers
				.stream()
				.map(a -> a.parse(type, urlPart))
				.filter(x -> x != null)
				.findFirst()
				.orElseThrow(
						() -> new RuntimeException(
								"No argument serializer found for " + type));
	}

}
