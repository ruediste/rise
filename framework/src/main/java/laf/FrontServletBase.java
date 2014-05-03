package laf;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import laf.requestProcessing.http.HttpRequestProcessingModule;

/**
 * Framework entry point
 */
public class FrontServletBase extends HttpServlet {

	@Inject
	HttpRequestProcessingModule httpRequestProcessingModule;

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		httpRequestProcessingModule.getHttpProcessor().process(req, resp);
	}

}
