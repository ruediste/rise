package com.github.ruediste.rise.nonReloadable.front;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.rise.nonReloadable.front.reload.FileChangeNotifier;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadableClassLoader;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Preconditions;

public abstract class FrontServletBase extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	Logger log;

	@Inject
	RestartCountHolder restartCountHolder;

	public volatile RestartableApplicationInfo currentApplicationInfo;

	private RestartableApplication fixedDynamicApplicationInstance;

	private Class<? extends RestartableApplication> dynamicApplicationInstanceClass;

	/**
	 * Construct using a {@link RestartableApplication} class. Enables reloading
	 */
	public FrontServletBase(
			Class<? extends RestartableApplication> dynamicApplicationInstanceClass) {
		Preconditions.checkNotNull(dynamicApplicationInstanceClass);
		this.dynamicApplicationInstanceClass = dynamicApplicationInstanceClass;
	}

	/**
	 * Construct using a fixed application instance. This will disable reloading
	 */
	public FrontServletBase(RestartableApplication fixedApplicationInstance) {
		Preconditions.checkNotNull(fixedApplicationInstance);
		this.fixedDynamicApplicationInstance = fixedApplicationInstance;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handle(req, resp, HttpMethod.GET);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handle(req, resp, HttpMethod.POST);
	}

	@Inject
	ApplicationEventQueue queue;

	private String applicationInstanceClassName;

	@Override
	public final void init() throws ServletException {
		try {
			initImpl();
		} catch (Exception e) {
			throw new RuntimeException("Error during initialization", e);
		}

		// continue in AET
		try {
			queue.submit(this::initInAET).get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Inject
	@Named("classPath")
	FileChangeNotifier notifier;

	@Inject
	@Named("dynamic")
	Provider<ReloadableClassLoader> dynamicClassLoaderProvider;

	@Inject
	Injector permanentInjector;

	@Inject
	DataBaseLinkRegistry registry;

	private void initInAET() {
		registry.initializeDataSources();

		try {
			if (fixedDynamicApplicationInstance == null) {
				// setup application reloading
				applicationInstanceClassName = dynamicApplicationInstanceClass
						.getName();
				notifier.addListener(trx -> reloadApplicationInstance());
			}

			// run initializers
			InitializerUtil.runInitializers(permanentInjector);

			if (fixedDynamicApplicationInstance != null) {
				notifier.close();
				// we are started with a fixed application instance, just use
				// it.
				// Primarily used for Unit Testing
				currentApplicationInfo = new RestartableApplicationInfo(
						fixedDynamicApplicationInstance, Thread.currentThread()
								.getContextClassLoader());
				fixedDynamicApplicationInstance.start(permanentInjector);
			} else {
				// application gets started through the initial file change
				// transaction
			}
		} catch (Throwable t) {
			log.error("Error during startup", t);
			System.exit(1);
		}
	}

	private void reloadApplicationInstance() {
		log.info("Reloading application instance ...");
		long startTime = System.currentTimeMillis();
		try {
			// close old application instance
			if (currentApplicationInfo != null) {
				currentApplicationInfo.application.close();

				// reload configuration for logback, if available
				{
					ILoggerFactory iLoggerFactory = LoggerFactory
							.getILoggerFactory();
					if (iLoggerFactory.getClass().getName()
							.equals("ch.qos.logback.classic.LoggerContext")) {

						// original code:
						// LoggerContext
						// loggerContext=(LoggerContext)LoggerFactory.getILoggerFactory()
						// ContextInitializer ci = new
						// ContextInitializer(loggerContext);
						// URL url = ci.findURLOfDefaultConfigurationFile(true);
						// loggerContext.reset();
						// ci.configureByResource(url);
						try {
							Class<?> cContextInitializer = Class
									.forName("ch.qos.logback.classic.util.ContextInitializer");
							Class<?> cLoggerContext = Class
									.forName("ch.qos.logback.classic.LoggerContext");
							Object loggerContext = iLoggerFactory;

							Object ci = cContextInitializer.getConstructor(
									cLoggerContext).newInstance(loggerContext);
							Object url1 = cContextInitializer.getMethod(
									"findURLOfDefaultConfigurationFile",
									boolean.class).invoke(ci, true);
							cLoggerContext.getMethod("reset").invoke(
									loggerContext);
							cContextInitializer.getMethod(
									"configureByResource", URL.class).invoke(
									ci, url1);
						} catch (Throwable t) {
							log.error(
									"Error updating logback configuration, continuing...",
									t);
						}
					}
				}
			}

			// create application instance
			RestartableApplication instance;

			Thread currentThread = Thread.currentThread();
			ClassLoader old = currentThread.getContextClassLoader();
			try {
				ReloadableClassLoader dynamicClassloader = dynamicClassLoaderProvider
						.get();
				currentThread.setContextClassLoader(dynamicClassloader);

				instance = (RestartableApplication) dynamicClassloader
						.loadClass(applicationInstanceClassName).newInstance();

				instance.start(permanentInjector);

				currentApplicationInfo = new RestartableApplicationInfo(
						instance, dynamicClassloader);

			} finally {
				currentThread.setContextClassLoader(old);
			}
			log.info("Reloading complete. Took "
					+ (System.currentTimeMillis() - startTime) + "ms");
			restartCountHolder.increment();
		} catch (Throwable t) {
			log.warn("Error loading application instance", t);
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

	private void handle(HttpServletRequest req, HttpServletResponse resp,
			HttpMethod method) throws IOException, ServletException {
		RestartableApplicationInfo info = currentApplicationInfo;
		if (info != null) {
			Thread currentThread = Thread.currentThread();
			ClassLoader old = currentThread.getContextClassLoader();
			try {
				currentThread.setContextClassLoader(info.classLoader);
				info.application.handle(req, resp, method);
			} finally {
				currentThread.setContextClassLoader(old);
			}
		} else {
			log.error("current application info is null");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"FrontServlet: current application info is null");
		}
	}

	public static class RestartableApplicationInfo {

		public RestartableApplication application;
		public ClassLoader classLoader;

		public RestartableApplicationInfo(RestartableApplication application,
				ClassLoader classLoader) {
			super();
			this.application = application;
			this.classLoader = classLoader;
		}

	}

}
