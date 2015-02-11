package com.github.ruediste.laf.core.entry;

import java.io.IOException;
import java.util.HashSet;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.classReload.DynamicClassLoader;
import com.github.ruediste.laf.core.classReload.Gate;
import com.github.ruediste.laf.core.guice.RequestData;
import com.google.inject.Injector;
import com.google.inject.Provider;

public abstract class FrontServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Logger log;

	@Inject
	Provider<DynamicClassLoader> loaderProvider;

	public volatile ApplicationInstanceInfo currentInstance;
	private Gate applicationInstanceInitiallyLoaded = new Gate();
	private ApplicationInstance fixedApplicationInstance;

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

	@Override
	public final void init() throws ServletException {
		try {
			initImpl();
		} catch (Exception e) {
			throw new RuntimeException("Error during initialization", e);
		}

		if (fixedApplicationInstance != null) {
			// we are started with a fixed application instance, just use it.
			// Primarily used for Unit Testing
			currentInstance = new ApplicationInstanceInfo(
					fixedApplicationInstance, Thread.currentThread()
							.getContextClassLoader());
			fixedApplicationInstance.start();
		} else {
			// normal initialization
			{
				Thread reloadThread = new Thread(
						new ApplicationInstanceReloader(),
						"Application Instance reload thread");
				reloadThread.setDaemon(true);
				reloadThread.start();
			}

			applicationInstanceInitiallyLoaded.pass();
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
			// set request data
			RequestData.setCurrent(new RequestData(req, resp, method));

			Thread currentThread = Thread.currentThread();
			ClassLoader old = currentThread.getContextClassLoader();
			try {
				currentThread
						.setContextClassLoader(currentInstance.classLoader);
				currentInstance.instance.handle(req, resp, method);
			} finally {
				currentThread.setContextClassLoader(old);
				RequestData.remove();
			}
		} else {
			log.warn("current application instance is null");
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

	private class ApplicationInstanceReloader implements Runnable {

		Gate reloadGate = new Gate(true);
		private boolean initialLoading = true;

		@Override
		public void run() {

			String applicationInstanceClassName;
			{
				Class<? extends ApplicationInstance> cls = getApplicationInstanceClass();
				if (cls == null) {
					throw new RuntimeException(
							"Please override getApplicationInstanceClass() or provide a fixed application instance");
				}
				applicationInstanceClassName = cls.getName();
			}
			while (true) {
				log.info("Waiting for reload trigger");

				reloadGate.pass();

				log.info("Reloading application instance");
				// wait a bit to consolidate changes
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				// close the gate, such that the changes can open it again
				reloadGate.close();

				try {
					// close current instance
					if (currentInstance != null) {
						currentInstance.instance.close();
					}
				} catch (Throwable t) {
					log.error("Error while closing current instance", t);
				}

				DynamicClassLoader cl;
				try {
					cl = loaderProvider.get();
					// create new class loader
					HashSet<String> projects = new HashSet<>();
					projects.add("test");
					cl.initialize(projects, new Runnable() {

						boolean fired;

						@Override
						public synchronized void run() {
							log.debug("class was changed ");

							// fire once only
							synchronized (this) {
								if (fired) {
									return;
								}
								fired = true;
							}
							cl.close();
							// when anything changes, update instance
							reloadGate.open();
						}
					});
				} catch (Throwable t) {
					log.error(
							"Error while creating new class loader, quiting reload loop. Restart server",
							t);
					throw t;
				}

				try {
					// create application instance
					ApplicationInstance instance;

					Thread currentThread = Thread.currentThread();
					ClassLoader old = currentThread.getContextClassLoader();
					try {
						currentThread.setContextClassLoader(cl);

						instance = (ApplicationInstance) cl.loadClass(
								applicationInstanceClassName).newInstance();

						currentInstance = new ApplicationInstanceInfo(instance,
								cl);

						instance.start();

					} finally {
						currentThread.setContextClassLoader(old);
					}
					log.info("Reloading complete");
				} catch (Throwable t) {
					log.warn("Error loading application instance", t);
				}

				if (initialLoading) {
					initialLoading = false;
					applicationInstanceInitiallyLoaded.open();
				}
			}
		}

	}

}
