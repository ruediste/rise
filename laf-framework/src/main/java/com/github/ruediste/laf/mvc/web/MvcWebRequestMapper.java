package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.web.PathInfo;
import com.github.ruediste.laf.mvc.ActionInvocation;

public interface MvcWebRequestMapper {

	/**
	 * Generate a HttpRequest for a given action invocation
	 */
	PathInfo generate(ActionInvocation<String> invocation);

	/**
	 * Prepare this mapper for use
	 */
	void initialize();

}
