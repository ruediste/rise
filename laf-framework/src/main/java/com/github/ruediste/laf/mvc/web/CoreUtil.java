package com.github.ruediste.laf.mvc.web;

import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.HttpService;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.core.web.PathInfo;

public class CoreUtil {

	@Inject
	CoreConfiguration coreConfiguration;

	@Inject
	HttpService httpService;

	public ActionInvocation<Object> toObjectInvocation(
			ActionInvocation<String> stringInvocation) {
		return toSupplierInvocation(stringInvocation).map(Supplier::get);
	}

	public ActionInvocation<Supplier<Object>> toSupplierInvocation(
			ActionInvocation<String> stringInvocation) {
		return stringInvocation.mapWithType(coreConfiguration::parseArgument);
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

	public String url(String pathInfo) {
		return url(new PathInfo(pathInfo));
	}

	public String url(PathInfo path) {
		return httpService.url(path);
	}

}
