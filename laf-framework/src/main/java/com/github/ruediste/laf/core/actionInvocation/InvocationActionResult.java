package com.github.ruediste.laf.core.actionInvocation;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.mvc.web.MvcWebActionPathBuilder;

/**
 * An action method returns an {@link ActionResult}. When using the
 * {@link MvcWebActionPathBuilder} to create {@link ActionInvocation}s, instances of
 * this class is used to represent the result.
 */
public class InvocationActionResult extends ActionInvocation<Object> implements
		ActionResult {

}
