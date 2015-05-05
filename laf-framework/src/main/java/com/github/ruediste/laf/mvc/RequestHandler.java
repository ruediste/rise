package com.github.ruediste.laf.mvc;

import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;


public interface RequestHandler {
	void handle(ActionInvocation<String> invocation);
}
