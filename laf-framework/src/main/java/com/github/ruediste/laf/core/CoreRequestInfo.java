package com.github.ruediste.laf.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.core.scopes.RequestScoped;
import com.github.ruediste.laf.core.web.HttpRenderResult;

@RequestScoped
public class CoreRequestInfo {
	private HttpRequest request;
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;
	private HttpRenderResult actionResult;

	private ActionInvocation<String> stringActionInvocation;

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

	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public HttpServletResponse getServletResponse() {
		return servletResponse;
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}

	public ServletContext getServletContext() {
		return servletRequest.getServletContext();
	}

}
