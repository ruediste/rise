package com.github.ruediste.laf.mvc.web;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.core.httpRequest.HttpRequestImpl;
import com.github.ruediste.laf.core.web.PathInfo;

@Singleton
public class MvcActionInvocationUtil {

	@Inject
	CoreConfiguration coreConfiguration;

	@Inject
	MvcWebConfiguration mvcWebConfiguration;

	@Inject
	CoreUtil coreUtil;

	public PathInfo toPathInfo(ActionInvocation<Object> invocation) {
		return mvcWebConfiguration.mapper().generate(
				coreUtil.toStringInvocation(invocation));
	}

	public HttpRequest toHttpRequest(ActionInvocation<Object> invocation) {
		return new HttpRequestImpl(toPathInfo(invocation));
	}
}
