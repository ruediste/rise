package laf;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.requestProcessing.http.HttpRequestProcessingService;

/**
 * Framework entry point
 */
public class FrontServletBase extends HttpServlet {

	@Inject
	HttpRequestProcessingService service;

	@Inject
	HttpServletResponseProducer httpServletResponseProducer;

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		httpServletResponseProducer.setResponse(resp);

		service.process(req, resp);
	}

}
