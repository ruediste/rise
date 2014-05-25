package laf;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.httpRequestProcessing.HttpRequestProcessorConfigurationValue;

/**
 * Framework entry point
 */
public class FrontServletBase extends HttpServlet {

	@Inject
	HttpRequestProcessorConfigurationValue processor;

	@Inject
	HttpServletResponseProducer httpServletResponseProducer;

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		httpServletResponseProducer.setResponse(resp);

		processor.get().process(req, resp);
	}

}
