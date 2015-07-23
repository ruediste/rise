package com.github.ruediste.rise.core;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.argumentSerializer.ArgumentSerializer;
import com.github.ruediste.rise.core.argumentSerializer.ClassArgumentSerializer;
import com.github.ruediste.rise.core.argumentSerializer.EntityArgumentSerializer;
import com.github.ruediste.rise.core.argumentSerializer.IntSerializer;
import com.github.ruediste.rise.core.argumentSerializer.LongSerializer;
import com.github.ruediste.rise.core.argumentSerializer.SerializableArgumentSerializer;
import com.github.ruediste.rise.core.argumentSerializer.SerializerHelper;
import com.github.ruediste.rise.core.argumentSerializer.StringSerializer;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvasBase;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.reflect.Reflection;

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

    /**
     * set {@link #basePackage} to the package of the given class
     */
    public void setBasePackage(Class<?> clazz) {
        basePackage = Reflection.getPackageName(clazz);
    }

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
     * The classloader used to load the classes in the dynamic class space
     */
    public ClassLoader dynamicClassLoader;

    public final LinkedList<Supplier<ArgumentSerializer>> argumentSerializerSuppliers = new LinkedList<>();

    public class SerializerSupplierRefs {
        final Supplier<ArgumentSerializer> longSerializerSupplier = () -> injector
                .getInstance(LongSerializer.class);
        final Supplier<ArgumentSerializer> intSerializerSupplier = () -> injector
                .getInstance(IntSerializer.class);
        final Supplier<ArgumentSerializer> stringSerializerSupplier = () -> injector
                .getInstance(StringSerializer.class);
        final Supplier<ArgumentSerializer> classSerializer = () -> injector
                .getInstance(ClassArgumentSerializer.class);
        final Supplier<ArgumentSerializer> entitySerializerSupplier = () -> injector
                .getInstance(EntityArgumentSerializer.class);
        final Supplier<ArgumentSerializer> serializableSerializerSupplier = () -> injector
                .getInstance(SerializableArgumentSerializer.class);

    }

    public final SerializerSupplierRefs serializerSupplierRefs = new SerializerSupplierRefs();

    {
        argumentSerializerSuppliers
                .add(serializerSupplierRefs.longSerializerSupplier);
        argumentSerializerSuppliers
                .add(serializerSupplierRefs.intSerializerSupplier);
        argumentSerializerSuppliers
                .add(serializerSupplierRefs.stringSerializerSupplier);
        argumentSerializerSuppliers.add(serializerSupplierRefs.classSerializer);
        argumentSerializerSuppliers
                .add(serializerSupplierRefs.entitySerializerSupplier);
        argumentSerializerSuppliers
                .add(serializerSupplierRefs.serializableSerializerSupplier);
    }

    private java.util.List<ArgumentSerializer> argumentSerializers;

    public String generateArgument(AnnotatedType type, Object value) {
        List<ArgumentSerializer> matchingFilters = getMatchingArgumentSerializers(type);

        if (matchingFilters.size() == 1) {
            Optional<String> result = matchingFilters.get(0).generate(type,
                    value);
            if (result.isPresent())
                return result.get();
        } else if (matchingFilters.size() > 1)
            for (int i = 0; i < matchingFilters.size(); i++) {
                ArgumentSerializer filter = matchingFilters.get(i);
                Optional<String> result = filter.generate(type, value);
                if (result.isPresent())
                    return SerializerHelper.generatePrefix(
                            Optional.of(Integer.toString(i)), result.get());
            }

        // no serializer matched
        throw new RuntimeException("No argument serializer found for "
                + type.getType() + " and value " + value);
    }

    private List<ArgumentSerializer> getMatchingArgumentSerializers(
            AnnotatedType type) {

        ArrayList<ArgumentSerializer> result = new ArrayList<>();
        loop: for (ArgumentSerializer s : argumentSerializers) {
            switch (s.canHandle(type)) {
            case CANNOT_HANDLE:
                break;
            case MIGHT_HANDLE:
                result.add(s);
                break;
            case WILL_HANDLE:
                result.add(s);
                break loop;
            default:
                throw new UnsupportedOperationException("Unknown case");

            }
        }
        return result;
    }

    public Supplier<Object> parseArgument(AnnotatedType type, String urlPart) {
        List<ArgumentSerializer> matchingFilters = getMatchingArgumentSerializers(type);
        if (matchingFilters.size() == 1)
            return matchingFilters.get(0).parse(type, urlPart);
        else if (matchingFilters.size() > 1) {
            Pair<Optional<String>, String> pair = SerializerHelper
                    .parsePrefix(urlPart);
            int idx = Integer.parseInt(pair.getA().get());
            return matchingFilters.get(idx).parse(type, pair.getB());
        }
        throw new RuntimeException("No argument serializer found for " + type);
    }

    public List<Function<ActionInvocation<String>, Optional<PathInfo>>> actionInvocationToPathInfoMappingFunctions = new ArrayList<>();

    public PathInfo toPathInfo(ActionInvocation<String> invocation) {
        for (Function<ActionInvocation<String>, Optional<PathInfo>> f : actionInvocationToPathInfoMappingFunctions) {
            Optional<PathInfo> result = f.apply(invocation);
            if (result.isPresent())
                return result.get();
        }
        throw new RuntimeException("No PathInfo generation function found for "
                + invocation);
    }

    public PathInfo restartQueryPathInfo = new PathInfo("/~riseRestartQuery");

    public String htmlContentType = "text/html;charset=utf-8";

    /**
     * when executed, fills the db with the fixture data
     */
    public Optional<Runnable> developmentFixtureLoader = Optional.empty();

    public void loadDevelopmentFixture() {
        developmentFixtureLoader.ifPresent(Runnable::run);
    }

    /**
     * Handler for request errors. If null, a plain 500 error code will be
     * returned
     */
    public RequestErrorHandler requestErrorHandler;

    public void handleRequestError(Throwable t) {
        if (requestErrorHandler == null)
            throw new RuntimeException(
                    "No error handler defined. Initialize CoreConfiguration.requestErrorHandler",
                    t);
        requestErrorHandler.handle();
    }

    /**
     * Factory for the {@link RiseCanvas} subtype used by the application
     */
    public Optional<Supplier<RiseCanvasBase<?>>> applicationCanvasFactory = Optional
            .empty();

    public RiseCanvasBase<?> createApplicationCanvas() {
        return applicationCanvasFactory
                .map(f -> f.get())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Initialize applicationCanvasFactory from your restartable application"));
    }

    public Optional<Function<HtmlCanvasTarget, BootstrapRiseCanvas<?>>> bootstrapCanvasFactory = Optional
            .empty();

    public BootstrapRiseCanvas<?> createBootstrapCanvas(HtmlCanvasTarget target) {
        return bootstrapCanvasFactory
                .map(f -> f.apply(target))
                .orElseThrow(
                        () -> new RuntimeException(
                                "Initialize bootstrapCanvasFactory from your restartable application"));
    }

    /**
     * Set the locale which is used by default
     */
    public Locale defaultLocale = Locale.ENGLISH;

    public Locale getDefaultLocale() {
        return defaultLocale;
    }
}
