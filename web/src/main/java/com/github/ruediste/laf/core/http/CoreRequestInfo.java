package com.github.ruediste.laf.core.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.laf.core.guice.RequestScoped;
import com.github.ruediste.laf.core.http.request.HttpRequest;

@RequestScoped
public class CoreRequestInfo {
	private HttpRequest request;
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;

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
