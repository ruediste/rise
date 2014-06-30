package laf;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import laf.base.configuration.ConfigurationValue;
import laf.http.HttpServletRequestProducer;
import laf.http.HttpServletResponseProducer;
import laf.http.requestProcessing.HttpRequestProcessorConfigurationParameter;

/**
 * Framework entry point
 */
public class FrontServletBase extends HttpServlet {

	@Inject
	ConfigurationValue<HttpRequestProcessorConfigurationParameter> processor;

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

		processor.value().get().process(req, resp);
	}

}
