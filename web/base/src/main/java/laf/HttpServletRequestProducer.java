package laf;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;

@RequestScoped
public class HttpServletRequestProducer {

	private HttpServletRequest request;

	@Produces
	@RequestScoped
	public HttpServletRequest produceResponse() {
		return request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}
