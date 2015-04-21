package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.scopes.RequestScoped;
import com.github.ruediste.laf.core.web.HttpRenderResult;
import com.github.ruediste.laf.mvc.ActionInvocation;

@RequestScoped
public class MvcWebRequestInfo {

	private ActionInvocation<String> stringActionInvocation;

	private HttpRenderResult actionResult;

	public MvcWebRequestInfo self() {
		return this;
	}

	public ActionInvocation<String> getStringActionInvocation() {
		return stringActionInvocation;
	}

	public void setStringActionInvocation(
			ActionInvocation<String> stringActionInvocation) {
		this.stringActionInvocation = stringActionInvocation;
	}

	public HttpRenderResult getActionResult() {
		return actionResult;
	}

	public void setActionResult(HttpRenderResult result) {
		this.actionResult = result;
	}
}
