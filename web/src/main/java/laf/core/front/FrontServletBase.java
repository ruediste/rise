package laf.core.front;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import laf.core.base.configuration.ConfigurationValue;
import laf.core.defaultConfiguration.HttpRequestParserChainCP;
import laf.core.http.CoreRequestInfo;
import laf.core.http.request.DelegatingHttpRequest;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;

/**
 * Framework entry point
 */
public class FrontServletBase extends HttpServlet {

	@Inject
	ConfigurationValue<HttpRequestParserChainCP> parserChain;

	@Inject
	CoreRequestInfo coreRequestInfo;

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
		DelegatingHttpRequest request = new DelegatingHttpRequest(req);

		coreRequestInfo.setRequest(request);
		coreRequestInfo.setServletRequest(req);
		coreRequestInfo.setServletResponse(resp);

		RequestParseResult<HttpRequest> parseResult = parserChain.value().get()
				.parse(request);
		if (parseResult == null) {
			throw new RuntimeException("unable to parse path "
					+ request.getPath());
		}
		parseResult.handle(request);
	}

}
