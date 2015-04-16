package com.github.ruediste.laf.core.front;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.front.reload.ClassPathWalker;
import com.github.ruediste.laf.core.front.reload.FileChangeNotifier;
import com.github.ruediste.laf.core.front.reload.SpaceAwareClassLoader;
import com.github.ruediste.laf.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;

public abstract class FrontServletBase extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	Logger log;

	public volatile ApplicationInstanceInfo currentInstance;

	private DynamicApplication fixedApplicationInstance;

	/**
	 * Set a fixed application instance. This will disable reloading
	 */
	public void setFixedApplicationInstance(
			DynamicApplication fixedApplicationInstance) {
		this.fixedApplicationInstance = fixedApplicationInstance;
	}

	/**
	 * Override for normal front servlets. Not required if a fixed
	 * {@link DynamicApplication} is used
	 */
	protected Class<? extends DynamicApplication> getApplicationInstanceClass() {
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
	ClassPathWalker scanner;

	@Inject
	FileChangeNotifier notifier;

	@Inject
	CoreConfiguration config;

	@Inject
	@Named("dynamic")
	Provider<SpaceAwareClassLoader> dynamicClassLoaderProvider;

	@Inject
	Injector permanentInjector;

	private void initInAET() {

		if (fixedApplicationInstance == null) {
			// setup application reloading
			applicationInstanceClassName = getApplicationInstanceClass()
					.getName();
			notifier.addListener(trx -> reloadApplicationInstance());
		}

		// run initializers
		InitializerUtil.runInitializers(permanentInjector);

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
			DynamicApplication instance;

			Thread currentThread = Thread.currentThread();
			ClassLoader old = currentThread.getContextClassLoader();
			try {
				currentThread.setContextClassLoader(cl);

				instance = (DynamicApplication) cl.loadClass(
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

		public DynamicApplication instance;
		public ClassLoader classLoader;

		public ApplicationInstanceInfo(DynamicApplication instance,
				ClassLoader classLoader) {
			super();
			this.instance = instance;
			this.classLoader = classLoader;
		}

	}

}
