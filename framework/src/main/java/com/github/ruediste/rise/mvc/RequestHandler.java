package com.github.ruediste.rise.mvc;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;


public interface RequestHandler {
	void handle(ActionInvocation<String> invocation);
}
