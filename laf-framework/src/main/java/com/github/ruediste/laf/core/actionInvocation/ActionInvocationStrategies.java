package com.github.ruediste.laf.core.actionInvocation;

import com.github.ruediste.laf.core.web.PathInfo;

/**
 * Contains various strategies set by the creator of an {@link ActionInvocation}
 */
public interface ActionInvocationStrategies {

	PathInfo generate(ActionInvocation<String> invocation);
}
