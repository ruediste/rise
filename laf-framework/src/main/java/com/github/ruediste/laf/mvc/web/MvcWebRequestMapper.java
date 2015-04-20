package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.mvc.ActionInvocation;

public interface MvcWebRequestMapper {

	/**
	 * Generate a HttpRequest for a given action invocation
	 */
	HttpRequest generate(ActionInvocation<String> invocation);

	/**
	 * Register the available controllers with the {@link PathInfoIndex}
	 */
	void registerControllers();

}
