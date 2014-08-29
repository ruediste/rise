package laf.component.core;

import laf.core.MethodInvocation;
import laf.core.base.ActionResult;

public class PathActionInvocation extends ActionInvocation<Object> implements
		ActionResult {

	public PathActionInvocation() {
		super();
	}

	public PathActionInvocation(MethodInvocation<Object> invocation) {
		super(invocation);
	}

}
