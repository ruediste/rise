package com.github.ruediste.laf.component.core;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.core.base.MethodInvocation;

public class PathActionInvocation extends ActionInvocation<Object> implements
		ActionResult {

	public PathActionInvocation() {
		super();
	}

	public PathActionInvocation(MethodInvocation<Object> invocation) {
		super(invocation);
	}

}
