package laf.core.http;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;

import laf.core.http.request.DelegatingHttpRequest;
import laf.core.http.request.HttpRequest;

@RequestScoped
public class HttpServletRequestProducer {

	private HttpServletRequest request;

	@Produces
	@RequestScoped
	public HttpServletRequest produceServletRequest() {
		return request;
	}

	@Produces
	@RequestScoped
	public HttpRequest produceRequest() {
		return new DelegatingHttpRequest(request);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}
