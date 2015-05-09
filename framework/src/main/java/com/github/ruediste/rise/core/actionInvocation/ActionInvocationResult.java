package com.github.ruediste.rise.core.actionInvocation;

import com.github.ruediste.rise.core.ActionResult;

/**
 * An action method returns an {@link ActionResult}. When using the
 * {@link ActionInvocationBuilder} to create {@link ActionInvocation}s, instances of
 * this class is used to represent the result.
 */
public class ActionInvocationResult extends ActionInvocation<Object> implements
		ActionResult {

}
