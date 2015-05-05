package com.github.ruediste.laf.core;

import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.core.web.PathInfo;

public interface RequestMapper {

	/**
	 * Generate a HttpRequest for a given action invocation
	 */
	PathInfo generate(ActionInvocation<String> invocation);

	/**
	 * Prepare this mapper for use
	 */
	void initialize();

}
