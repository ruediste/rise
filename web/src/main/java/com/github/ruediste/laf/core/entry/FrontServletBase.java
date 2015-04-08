package com.github.ruediste.laf.core.entry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.inject.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.classReload.*;
import com.github.ruediste.laf.core.defaultConfiguration.DefaultConfiguration;
import com.github.ruediste.salta.jsr330.Injector;

public abstract class FrontServletBase extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	Logger log;

	public volatile ApplicationInstanceInfo currentInstance;

	private ApplicationInstance fixedApplicationInstance;

	/**
	 * Set a fixed application instance. This will disable reloading
	 */
	public void setFixedApplicationInstance(
			ApplicationInstance fixedApplicationInstance) {
		this.fixedApplicationInstance = fixedApplicationInstance;
	}

	/**
	 * Override for normal front servlets. Not required if a fixed
	 * {@link ApplicationInstance} is used
	 */
	protected Class<? extends ApplicationInstance> getApplicationInstanceClass() {
		return null;
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

	@Inject
	ApplicationInitializer applicationInitializer;

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
	Scanner scanner;

	@Inject
	FileChangeNotifier notifier;

	@Inject
	DefaultConfiguration config;

	@Inject
	@Named("dynamic")
	Provider<SpaceAwareClassLoader> dynamicClassLoaderProvider;

	private void initInAET() {
		// run initializer
		applicationInitializer.initialize();

		if (fixedApplicationInstance == null) {
			// load application instance
			{
				Class<? extends ApplicationInstance> cls = getApplicationInstanceClass();
				applicationInstanceClassName = cls.getName();
			}

			notifier.addListener(trx -> reloadApplicationInstance());
		}
		Set<Path> rootDirs = new HashSet<>();
		scanner.initialize((rootDirectory, classloader) -> rootDirs
				.add(rootDirectory));
		scanner.scan(Thread.currentThread().getContextClassLoader());

		notifier.start(rootDirs, config.fileChangeSettleDelayMs);

		if (fixedApplicationInstance != null) {
			notifier.close();
			// we are started with a fixed application instance, just use it.
			// Primarily used for Unit Testing
			currentInstance = new ApplicationInstanceInfo(
					fixedApplicationInstance, Thread.currentThread()
							.getContextClassLoader());
			fixedApplicationInstance.start();
		} else {
			// application gets started through the initial file change
			// transaction
		}
	}

	private void reloadApplicationInstance() {
		log.info("Reloading application instance ...");
		SpaceAwareClassLoader cl = dynamicClassLoaderProvider.get();
		try {
			// create application instance
			ApplicationInstance instance;

			Thread currentThread = Thread.currentThread();
			ClassLoader old = currentThread.getContextClassLoader();
			try {
				currentThread.setContextClassLoader(cl);

				instance = (ApplicationInstance) cl.loadClass(
						applicationInstanceClassName).newInstance();

				currentInstance = new ApplicationInstanceInfo(instance, cl);

				instance.start();

			} finally {
				currentThread.setContextClassLoader(old);
			}
			log.info("Reloading complete");
		} catch (Throwable t) {
			log.warn("Error loading application instance", t);
		}
	}

	/**
	 * Overide for custom initialization. Run before the ApplicationInstance is
	 * created/started. Needs at least to create an {@link Injector} and inject
	 * this instance.
	 */
	protected abstract void initImpl() throws Exception;

	@Override
	public void destroy() {
	}

	private void handle(HttpServletRequest req, HttpServletResponse resp,
			HttpMethod method) throws IOException, ServletException {
		if (currentInstance != null) {
			Thread currentThread = Thread.currentThread();
			ClassLoader old = currentThread.getContextClassLoader();
			try {
				currentThread
						.setContextClassLoader(currentInstance.classLoader);
				currentInstance.instance.handle(req, resp, method);
			} finally {
				currentThread.setContextClassLoader(old);
			}
		} else {
			log.error("current application instance is null");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"FrontServlet: current application instance is null");
		}
	}

	public static class ApplicationInstanceInfo {

		public ApplicationInstance instance;
		public ClassLoader classLoader;

		public ApplicationInstanceInfo(ApplicationInstance instance,
				ClassLoader classLoader) {
			super();
			this.instance = instance;
			this.classLoader = classLoader;
		}

	}

}
