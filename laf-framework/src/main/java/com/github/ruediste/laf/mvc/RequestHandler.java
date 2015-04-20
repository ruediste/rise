package com.github.ruediste.laf.mvc;


public interface RequestHandler {
	void handle(ActionInvocation<String> invocation);
}
