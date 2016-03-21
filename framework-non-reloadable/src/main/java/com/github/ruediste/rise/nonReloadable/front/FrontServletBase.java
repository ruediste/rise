package com.github.ruediste.rise.nonReloadable.front;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.c3java.linearization.JavaC3;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder.Holder;
import com.github.ruediste.rise.nonReloadable.front.CurrentRestartableApplicationHolder.RestartableApplicationInfo;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadableClassLoader;
import com.github.ruediste.rise.nonReloadable.front.reload.ResourceChangeNotifier;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;
import com.github.ruediste.salta.jsr330.SaltaModule;
import com.github.ruediste.salta.standard.Stage;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

public abstract class FrontServletBase extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * available without injection
     */
    private static Logger log = LoggerFactory.getLogger(FrontServletBase.class);

    @Inject
    @Named("dynamic")
    Provider<ReloadableClassLoader> reloadableClassloaderProvider;

    @Inject
    Injector nonRestartableInjector;

    @Inject
    DataBaseLinkRegistry dataBaseLinkRegistry;

    @Inject
    CoreConfigurationNonRestartable configurationNonRestartable;

    @Inject
    RestartCountHolder restartCountHolder;

    @Inject
    ApplicationEventQueue queue;

    @Inject
    ResourceChangeNotifier notifier;

    @Inject
    CurrentRestartableApplicationHolder appHolder;

    private String applicationInstanceClassName;

    private StartupErrorHandler startupErrorHandler;

    private volatile Throwable startupError;

    private ApplicationStage stage;

    private Stopwatch startupStopwatch;
    private boolean isInitialStartup = true;

    private boolean useReloadableClassloader;

    /**
     * Construct using a {@link RestartableApplication} class. Enables reloading
     */
    public FrontServletBase(Class<? extends RestartableApplication> restartableApplicationInstanceClass) {
        this(restartableApplicationInstanceClass, true);
    }

    /**
     * Construct using a {@link RestartableApplication} class.
     * 
     * @param useReloadableClassloader
     *            if set to true, a separate class loader (
     *            {@link ReloadableClassLoader}) is used to load reloadable
     *            classes. This enables to apply changes without restarting the
     *            server. For testing purposes, the separate class loader causes
     *            issues, therefore the option to not use it.
     */
    public FrontServletBase(Class<? extends RestartableApplication> restartableApplicationInstanceClass,
            boolean useReloadableClassloader) {
        this.useReloadableClassloader = useReloadableClassloader;
        Preconditions.checkNotNull(restartableApplicationInstanceClass);
        applicationInstanceClassName = restartableApplicationInstanceClass.getName();
    }

    /**
     * Create an injector an inject this instance
     */
    protected void createInjector(ApplicationStage stage, SaltaModule... modules) {
        setStage(stage);
        Salta.createInjector(stage.getSaltaStage(), modules).injectMembers(this);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp, HttpMethod.GET);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp, HttpMethod.POST);
    }

    /**
     * Hook to change the {@link StartupErrorHandler}
     */
    protected StartupErrorHandler createStartupErrorHandler() {
        return new DefaultStartupErrorHandler();
    }

    @Override
    public final void init() throws ServletException {
        startupStopwatch = Stopwatch.createStarted();
        try {
            startupErrorHandler = createStartupErrorHandler();

            initImpl();

            // continue in AET
            queue.submit(this::initInAET).get();

        } catch (Throwable t) {
            log.error("Error during non-restartable startup", t);
            startupError = t;
        }
    }

    private void initInAET() {
        // setup application reloading
        notifier.addListener(trx -> reloadApplicationInstance());

        // run initializers
        InitializerUtil.runInitializers(nonRestartableInjector);

        // application gets started through the initial file change
        // transaction
    }

    private void reloadApplicationInstance() {
        if (!isInitialStartup) {
            StartupTimeLogger.clear();
            startupStopwatch = Stopwatch.createStarted();
            log.info("Reloading application instance ...");
        } else {
            log.info("Loading application instance ...");
        }
        try {
            // close old application instance

            if (appHolder.info() != null) {
                appHolder.info().application.close();
                appHolder.clearCurrentApplication();
                dataBaseLinkRegistry.closePersistenceUnitManagers();
            }

            // avoid classloader leaks by clearing the caches
            JavaC3.clearCache();
            PropertyUtil.clearCache();

            // create application instance
            RestartableApplication instance;

            Thread currentThread = Thread.currentThread();
            ClassLoader old = currentThread.getContextClassLoader();
            try {
                ClassLoader reloadableClassLoader;
                if (useReloadableClassloader)
                    reloadableClassLoader = reloadableClassloaderProvider.get();
                else
                    reloadableClassLoader = getClass().getClassLoader();
                currentThread.setContextClassLoader(reloadableClassLoader);

                instance = (RestartableApplication) reloadableClassLoader.loadClass(applicationInstanceClassName)
                        .newInstance();
                RestartableApplicationInfo info = new RestartableApplicationInfo(instance, reloadableClassLoader);
                appHolder.setCurrentApplication(info);

                instance.start(nonRestartableInjector);
                info.started = true;

            } finally {
                currentThread.setContextClassLoader(old);
            }
            restartCountHolder.increment();
            startupError = null;
            StartupTimeLogger.stopAndLog(isInitialStartup ? "Total Startup Time" : "Total Reload Time",
                    startupStopwatch);
            StartupTimeLogger.writeTimesToLog(isInitialStartup ? "Initial Startup Times" : "Reload Times");
            isInitialStartup = false;
        } catch (Throwable t) {
            log.warn("Error loading application instance", t);
            if (configurationNonRestartable != null)
                configurationNonRestartable.getStackTraceFilter().filter(t);
            startupError = t;
        }
    }

    /**
     * Overide for custom initialization. Executed at the very beginning of the
     * application startup. Needs at least to create an {@link Injector} and
     * inject this instance.
     */
    protected abstract void initImpl() throws Exception;

    @Override
    public void destroy() {
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp, HttpMethod method)
            throws IOException, ServletException {
        log.debug("Handling request to {} method: {}", req.getRequestURL(), method);
        if (startupError != null) {
            startupErrorHandler.handle(startupError, req, resp);
            return;
        }

        RestartableApplicationInfo info = appHolder.info();
        if (info != null || !info.started) {

            Thread currentThread = Thread.currentThread();
            Holder oldInjectors = InjectorsHolder.setInjectors(nonRestartableInjector,
                    info.application.getRestartableInjector());
            ClassLoader old = currentThread.getContextClassLoader();
            try {
                currentThread.setContextClassLoader(info.reloadableClassLoader);
                info.application.handle(req, resp, method);
            } finally {
                currentThread.setContextClassLoader(old);
                InjectorsHolder.restoreInjectors(oldInjectors);
            }
        } else {
            log.error("current application info is null");

            RuntimeException e = info == null ? new RuntimeException("current application info is null")
                    : new RuntimeException("Current application is not started");
            if (configurationNonRestartable != null)
                configurationNonRestartable.getStackTraceFilter().filter(e);
            startupErrorHandler.handle(e, req, resp);
        }
    }

    /**
     * Set the current {@link Stage}. Can be invoked from subclass before
     * initializing Salta. Will be called again during injection.
     */
    @PostConstruct
    protected void setStage(ApplicationStage stage) {
        this.stage = stage;
        startupErrorHandler.setStage(stage);
    }

    public ApplicationStage getStage() {
        return stage;
    }

    public Injector getNonRestartableInjector() {
        return nonRestartableInjector;
    }

    public Injector getCurrentRestartableInjector() {
        return appHolder.getCurrentRestartableInjector();
    }

    @PostConstruct
    private void postConstruct(Injector injector) {
        injector.injectMembers(startupErrorHandler);
    }
}
