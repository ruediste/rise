package laf.core.http;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import laf.core.base.configuration.ConfigurationValue;
import laf.core.defaultConfiguration.HttpRequestParserChainCP;
import laf.core.http.request.DelegatingHttpRequest;

/**
 * Framework entry point
 */
public class FrontServletBase extends HttpServlet {

	@Inject
	ConfigurationValue<HttpRequestParserChainCP> parserChain;

	@Inject
	HttpServletResponseProducer httpServletResponseProducer;

	@Inject
	HttpServletRequestProducer httpServletRequestProducer;

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handle(req, resp);
	}

	private void handle(HttpServletRequest req, HttpServletResponse resp) {
		httpServletRequestProducer.setRequest(req);
		httpServletResponseProducer.setResponse(resp);

		DelegatingHttpRequest request = new DelegatingHttpRequest(req);
		parserChain.value().get().parse(request).handle(request);
	}

}
