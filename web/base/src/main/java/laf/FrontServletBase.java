package laf;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import laf.configuration.ConfigurationValue;
import laf.httpRequestProcessing.HttpRequestProcessorConfigurationParameter;

/**
 * Framework entry point
 */
public class FrontServletBase extends HttpServlet {

	@Inject
	ConfigurationValue<HttpRequestProcessorConfigurationParameter> processor;

	@Inject
	HttpServletResponseProducer httpServletResponseProducer;

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		httpServletResponseProducer.setResponse(resp);

		processor.value().get().process(req, resp);
	}

}
