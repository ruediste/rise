package com.github.ruediste.rise.core.front;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentPageHandleRepository;
import com.github.ruediste.rise.component.PageHandle;
import com.github.ruediste.rise.component.PageScopeCleaner;
import com.github.ruediste.rise.component.PageScopeManager;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.httpRequest.DelegatingHttpRequest;
import com.github.ruediste.rise.core.scopes.HttpScopeManager;
import com.github.ruediste.rise.core.scopes.RequestScopeEvents;
import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.front.HttpMethod;
import com.github.ruediste.rise.nonReloadable.front.RestartableApplication;
import com.github.ruediste.rise.nonReloadable.front.reload.Reloadable;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.Stage;

/**
 * Instance of an application. Will be reloaded when the application is changed.
 */
@Reloadable
public abstract class RestartableApplicationBase implements RestartableApplication {

	@Inject
	Logger log;

	@Inject
	Stage stage;

	@Inject
	CoreConfiguration config;

	@Inject
	CoreConfigurationNonRestartable configNonRestartable;

	@Inject
	Injector injector;

	@Inject
	HttpScopeManager scopeManager;

	@Inject
	CoreRequestInfo info;

	@Inject
	RequestScopeEvents requestScopeEvents;

	@Inject
	PageScopeCleaner pageScopeCleaner;

	@Inject
	PageScopeManager pageScopeManager;

	@Inject
	ComponentPageHandleRepository componentPageHandleRepository;

	@Inject
	HttpScopeManager httpScopeManager;

	@Inject
	ComponentPage page;

	@Override
	public final void start(Injector nonRestartableInjector) {
		startImpl(nonRestartableInjector);
		InitializerUtil.runInitializers(injector);
		pageScopeCleaner.start();
	}

	/**
	 * Start the application. Needs to at least create an {@link Injector} and
	 * inject this instance
	 */
	protected abstract void startImpl(Injector nonRestartableInjector);

	private static class NoLoggingRequestException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NoLoggingRequestException(String msg) {
			super(msg);
		}

	}

	@Override
	public final void handle(HttpServletRequest request, HttpServletResponse response, HttpMethod method)
			throws IOException, ServletException {
		try {
			scopeManager.enter(request, response);
			DelegatingHttpRequest httpRequest = new DelegatingHttpRequest(request);

			info.setServletResponse(response);
			info.setServletRequest(request);
			info.setRequest(httpRequest);

			try {
				RequestParseResult parseResult = config.parse(httpRequest);
				if (parseResult == null) {
					String msg = "No Request Parser found for " + httpRequest.getPathInfo();
					log.error(msg);
					throw new NoLoggingRequestException(msg);
				} else
					parseResult.handle();
			} catch (Throwable t) {
				configNonRestartable.getStackTraceFilter().filter(t);

				if (!(t instanceof NoLoggingRequestException))
					log.error("Error while handling request to " + request.getRequestURL(), t);
				info.setRequestError(t);
				try {
					config.handleRequestError(t);
				}
				// handler for errors thrown while handling a request error
				catch (Throwable t1) {
					log.error("Error in RequestErrorHandler", t1);
					if (stage == Stage.PRODUCTION)
						// no information revealed in production
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					else {
						// just throw the exception and let the container
						// generate a page for it
						throw t1;
					}
				}
			}
		} finally {
			try {
				requestScopeEvents.fireDestroyed();
			} finally {
				scopeManager.exit();
			}
		}
	}

	@Override
	public final void close() {
		pageScopeCleaner.stop();
		// destroy all page scopes
		httpScopeManager.runInEachSessionScope(() -> {
			for (PageHandle handle : componentPageHandleRepository.getPageHandles()) {
				pageScopeManager.inScopeDo(handle.pageScopeState, () -> {
					page.destroy();
				});
			}
		});
		closeImpl();
		reloadLogback();
	}

	void reloadLogback() {
		// reload the logback configuration, without introducing a dependency on
		// logback
		ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
		if (iLoggerFactory.getClass().getName().equals("ch.qos.logback.classic.LoggerContext")) {

			// original code:
			// LoggerContext
			// loggerContext=(LoggerContext)LoggerFactory.getILoggerFactory()
			// ContextInitializer ci = new
			// ContextInitializer(loggerContext);
			// URL url = ci.findURLOfDefaultConfigurationFile(true);
			// loggerContext.reset();
			// ci.configureByResource(url);
			try {
				Class<?> cContextInitializer = Class.forName("ch.qos.logback.classic.util.ContextInitializer");
				Class<?> cLoggerContext = Class.forName("ch.qos.logback.classic.LoggerContext");
				Object loggerContext = iLoggerFactory;

				Object ci = cContextInitializer.getConstructor(cLoggerContext).newInstance(loggerContext);
				Object url1 = cContextInitializer.getMethod("findURLOfDefaultConfigurationFile", boolean.class)
						.invoke(ci, true);
				cLoggerContext.getMethod("reset").invoke(loggerContext);
				cContextInitializer.getMethod("configureByResource", URL.class).invoke(ci, url1);
			} catch (Throwable t) {
				log.error("Error updating logback configuration, continuing...", t);
			}
		}
	}

	/**
	 * Convenience method to inject the members of an instance
	 */
	protected final void injectMembers(Object instance) {
		injector.injectMembers(instance);
	}

	/**
	 * Called when the dynamic application is shut down
	 */
	protected void closeImpl() {
	}

	@Override
	public Injector getRestartableInjector() {
		return injector;
	}
}
