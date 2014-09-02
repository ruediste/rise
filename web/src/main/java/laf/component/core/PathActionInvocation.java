package laf.component.core;

import laf.core.base.ActionResult;
import laf.core.base.MethodInvocation;

public class PathActionInvocation extends ActionInvocation<Object> implements
		ActionResult {

	public PathActionInvocation() {
		super();
	}

	public PathActionInvocation(MethodInvocation<Object> invocation) {
		super(invocation);
	}

}
