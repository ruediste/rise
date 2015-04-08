package com.github.ruediste.laf.core.entry;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.laf.core.defaultConfiguration.DefaultConfiguration;
import com.github.ruediste.laf.core.guice.HttpScopeManager;
import com.github.ruediste.laf.core.http.request.DelegatingHttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParseResult;

/**
 * Instance of an application. Will be reloaded when the application is changed.
 */
public abstract class ApplicationInstanceBase implements ApplicationInstance {

	@Inject
	DefaultConfiguration config;

	@Override
	public final void start() {
		startImpl();
	}

	protected void startImpl() {

	}

	@Inject
	HttpScopeManager scopeManager;

	@Override
	public final void handle(HttpServletRequest request,
			HttpServletResponse response, HttpMethod method)
			throws IOException, ServletException {
		try {
			scopeManager.enter(request, response);
			DelegatingHttpRequest httpRequest = new DelegatingHttpRequest(
					request);
			RequestParseResult parseResult = config.parse(httpRequest);
			if (parseResult == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND,
						"No Request Parser found");
			}
			parseResult.handle();
		} finally {
			scopeManager.exit();
		}
	}

	@Override
	public final void close() {
		closeImpl();
	}

	protected void closeImpl() {

	}
}
