package com.github.ruediste.laf.mvc.web;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.core.httpRequest.HttpRequestImpl;
import com.github.ruediste.laf.core.web.PathInfo;
import com.github.ruediste.laf.mvc.ActionInvocation;

@Singleton
public class ActionInvocationUtil {

	@Inject
	CoreConfiguration coreConfiguration;

	@Inject
	MvcWebConfiguration mvcWebConfiguration;

	public PathInfo toPathInfo(ActionInvocation<Object> invocation) {
		return mvcWebConfiguration.mapper().generate(
				toStringInvocation(invocation));
	}

	public HttpRequest toHttpRequest(ActionInvocation<Object> invocation) {
		return new HttpRequestImpl(toPathInfo(invocation));
	}

	public ActionInvocation<String> toStringInvocation(
			ActionInvocation<Object> invocation) {

		try {
			return invocation.mapWithType(coreConfiguration::generateArgument);
		} catch (Throwable t) {
			throw new RuntimeException("Error while generating arguments of "
					+ invocation, t);
		}
	}

	public ActionInvocation<Object> toObjectInvocation(
			ActionInvocation<String> stringInvocation) {
		return toSupplierInvocation(stringInvocation).map(Supplier::get);
	}

	public ActionInvocation<Supplier<Object>> toSupplierInvocation(
			ActionInvocation<String> stringInvocation) {
		return stringInvocation.mapWithType(coreConfiguration::parseArgument);
	}
}
