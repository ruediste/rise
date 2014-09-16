package laf.core.http;

import javax.enterprise.context.RequestScoped;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.core.http.request.HttpRequest;
import laf.core.web.resource.ResourceRequestHandler;

@RequestScoped
public class CoreRequestInfo {
	private HttpRequest request;
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;
	private ResourceRequestHandler resourceRequestHandler;

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

	public void setResourceRequestHandler(
			ResourceRequestHandler resourceRequestHandler) {
		this.resourceRequestHandler = resourceRequestHandler;

	}

	public ResourceRequestHandler getResourceRequestHandler() {
		return resourceRequestHandler;
	}
}
