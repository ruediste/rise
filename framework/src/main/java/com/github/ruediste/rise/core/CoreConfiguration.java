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
import javax.inject.Provider;
import javax.inject.Singleton;

import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
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
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.Stage;

/**
 * Defines the default configuration of the framework.
 */
@Singleton
public class CoreConfiguration {

    @Inject
    Logger log;

    @Inject
    Injector injector;

    @Inject
    CoreConfigurationNonRestartable configNonRestartable;

    @Inject
    ApplicationStage stage;

    private <T> T get(Class<T> cls) {
        return injector.getInstance(cls);
    }

    public String controllerSuffix = "Controller";

    /**
     * Supplier create a name mapper. The name mapper is used to map a class to
     * it's name
     */
    public Supplier<Function<ClassNode, String>> controllerNameMapperSupplier = () -> {
        DefaultClassNameMapping mapping = get(DefaultClassNameMapping.class);
        mapping.initialize(configNonRestartable.getBasePackage(),
                controllerSuffix);
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
     * This is the request parser using the {@link PathInfoIndex} to parse a
     * request. initially added to {@link #requestParsers}
     */
    public RequestParser pathInfoIndexRequestParser;

    @PostConstruct
    private void setupRequestParsers(PathInfoIndex pathInfoIndex) {
        pathInfoIndexRequestParser = request -> {
            RequestParser parser = pathInfoIndex
                    .getHandler(request.getPathInfo());
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

    @Inject
    public ApplicationStage applicationStage;

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
        List<ArgumentSerializer> matchingFilters = getMatchingArgumentSerializers(
                type);

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
                continue loop;
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
        List<ArgumentSerializer> matchingFilters = getMatchingArgumentSerializers(
                type);
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

    public List<Function<Class<?>, Optional<RequestMapper>>> requestMapperProviders = new ArrayList<>();

    public RequestMapper getRequestMapper(Class<?> controllerClass) {
        for (Function<Class<?>, Optional<RequestMapper>> provider : requestMapperProviders) {
            Optional<RequestMapper> mapper = provider.apply(controllerClass);
            if (mapper.isPresent())
                return mapper.get();
        }
        throw new RuntimeException(
                "No request mapper found for controller class "
                        + controllerClass.getName());
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
     * Drop and create the databases and load the development fixture. Only
     * allowed in development mode.
     */
    public void recreateDatabases() {
        if (stage == ApplicationStage.DEVELOPMENT) {
            log.info("Dropping and Creating DB schemas ...");
            injector.getInstance(DataBaseLinkRegistry.class)
                    .dropAndCreateSchemas();
            loadDevelopmentFixture();
        } else {
            throw new RuntimeException(
                    "Can recreate databases in development mode only");
        }
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
        return applicationCanvasFactory.map(f -> f.get())
                .orElseThrow(() -> new RuntimeException(
                        "Initialize applicationCanvasFactory from your restartable application"));
    }

    public Optional<Function<HtmlCanvasTarget, BootstrapRiseCanvas<?>>> bootstrapCanvasFactory = Optional
            .empty();

    public BootstrapRiseCanvas<?> createBootstrapCanvas(
            HtmlCanvasTarget target) {
        return bootstrapCanvasFactory.map(f -> f.apply(target))
                .orElseThrow(() -> new RuntimeException(
                        "Initialize bootstrapCanvasFactory from your restartable application"));
    }

    /**
     * Set the locale which is used by default
     */
    public Locale defaultLocale = Locale.ENGLISH;

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * If true, the 'data-test-name' attributes are written to the responses
     */
    public boolean renderTestName;

    public boolean isRenderTestName() {
        return renderTestName;
    }

    @PostConstruct
    void postContruct() {
        renderTestName = applicationStage != ApplicationStage.PRODUCTION;
    }

    @Inject
    Provider<CoreRequestInfo> coreRequestInfo;

    @Inject
    Provider<CoreUtil> coreUtil;

    public String rememberMeCookieName = "riseRememberMe";

    public boolean doUrlSigning = true;

    /**
     * The number of bytes to use for the URL signature. The SIGN parameter in
     * the URL will be about three times that long (Base64 encoding, salt, hash)
     */
    public int urlSignatureBytes = 20;

    /**
     * Path info used to run unit tests within the server. Set to null or the
     * empty string to disable this feature. By default set to '/~unitTest' in
     * all stages except production.
     */
    public String unitTestCodeRunnerPathInfo;

    @PostConstruct
    private void setupUnitTestCodeRunnerPathInfo(Stage stage) {
        if (stage != Stage.PRODUCTION)
            unitTestCodeRunnerPathInfo = "/~unitTest";
    }

    public String translationsResourceBundleName = "translations/translations";

    public String getTranslationsResourceBundleName() {
        return translationsResourceBundleName;
    }
}
