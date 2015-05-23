package com.github.ruediste.rise.core;

import java.util.function.Function;

import javax.inject.Inject;

import com.github.ruediste.rise.core.httpRequest.DelegatingHttpRequest;
import com.github.ruediste.rise.core.web.PathInfo;

public class DefaultRequestErrorHandler implements RequestErrorHandler {

	@Inject
	CoreConfiguration config;

	@Inject
	CoreRequestInfo info;

	@Inject
	CoreUtil util;

	private PathInfo errorHandlerPath;

	public DefaultRequestErrorHandler initialize(
			Function<CoreUtil, ActionResult> errorHandlerPathGenerator) {
		util.toPathInfo(errorHandlerPathGenerator.apply(util));
		return this;
	}

	@Override
	public void handle() {
		DelegatingHttpRequest errorHandlerRequest = new DelegatingHttpRequest(
				info.getServletRequest()) {
			@Override
			public String getPathInfo() {
				return errorHandlerPath.getValue();
			};
		};
		info.setRequest(errorHandlerRequest);
		config.parse(errorHandlerRequest).handle();
	}
}
