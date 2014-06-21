package laf;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;

import laf.http.request.DelegatingHttpRequest;
import laf.http.request.HttpRequest;

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
