package com.github.ruediste.rise.core.front;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.front.DynamicApplication;
import com.github.ruediste.rise.core.front.HttpMethod;
import com.github.ruediste.rise.core.httpRequest.DelegatingHttpRequest;
import com.github.ruediste.rise.core.scopes.HttpScopeManager;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Instance of an application. Will be reloaded when the application is changed.
 */
public abstract class DynamicApplicationBase implements DynamicApplication {

	@Inject
	CoreConfiguration config;

	@Inject
	Injector injector;

	@Inject
	HttpScopeManager scopeManager;

	@Inject
	CoreRequestInfo info;

	@Override
	public final void start(Injector permanentInjector) {
		startImpl(permanentInjector);
		InitializerUtil.runInitializers(injector);
	}

	/**
	 * Start the application. Needs to at least create an {@link Injector} and
	 * inject this instance
	 */
	protected abstract void startImpl(Injector permanentInjector);

	@Override
	public final void handle(HttpServletRequest request,
			HttpServletResponse response, HttpMethod method)
			throws IOException, ServletException {
		try {
			scopeManager.enter(request, response);
			DelegatingHttpRequest httpRequest = new DelegatingHttpRequest(
					request);

			info.setServletResponse(response);
			info.setServletRequest(request);
			info.setRequest(httpRequest);

			RequestParseResult parseResult = config.parse(httpRequest);
			if (parseResult == null) {
				response.sendError(
						HttpServletResponse.SC_NOT_FOUND,
						"No Request Parser found for "
								+ httpRequest.getPathInfo());
			} else
				parseResult.handle();
		} finally {
			scopeManager.exit();
		}
	}

	@Override
	public final void close() {
		closeImpl();
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
		HashMap<String, EntityManagerSetupImpl> emsetupimpls = EntityManagerFactoryProvider.emSetupImpls;
		synchronized (emsetupimpls) {
			for (EntityManagerSetupImpl setup : new ArrayList<>(
					emsetupimpls.values())) {
				setup.undeploy();
			}
		}

	}
}
