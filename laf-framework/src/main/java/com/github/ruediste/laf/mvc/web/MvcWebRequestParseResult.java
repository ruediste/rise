package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.RequestParseResult;
import com.github.ruediste.laf.mvc.ActionInvocation;

public class MvcWebRequestParseResult implements RequestParseResult {

	private MvcWebConfiguration config;
	private final ActionInvocation<String> invocation;

	public MvcWebRequestParseResult(MvcWebConfiguration config,
			ActionInvocation<String> invocation) {
		this.config = config;
		this.invocation = invocation;
	}

	@Override
	public void handle() {
		config.requestHandler.handle(invocation);
	}

	public ActionInvocation<String> getInvocation() {
		return invocation;
	}

}
