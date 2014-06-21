package laf;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletResponse;

@RequestScoped
public class HttpServletResponseProducer {

	private HttpServletResponse response;

	@Produces
	@RequestScoped
	public HttpServletResponse produceResponse() {
		return getResponse();
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
}
