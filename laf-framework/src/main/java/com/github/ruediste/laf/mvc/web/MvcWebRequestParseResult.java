package com.github.ruediste.laf.mvc.web;

import java.util.function.Consumer;

import com.github.ruediste.laf.core.RequestParseResult;
import com.github.ruediste.laf.mvc.ActionInvocation;

public class MvcWebRequestParseResult implements RequestParseResult {

	private Consumer<ActionInvocation<String>> handler;
	private final ActionInvocation<String> invocation;

	public MvcWebRequestParseResult(ActionInvocation<String> invocation,
			Consumer<ActionInvocation<String>> handler) {
		this.invocation = invocation;
		this.handler = handler;
	}

	@Override
	public void handle() {
		handler.accept(invocation);
	}

	public ActionInvocation<String> getInvocation() {
		return invocation;
	}

}
