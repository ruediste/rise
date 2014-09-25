package laf.core.front;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import laf.core.base.configuration.ConfigurationValue;
import laf.core.defaultConfiguration.HttpRequestParserChainCP;
import laf.core.defaultConfiguration.ResourceRequestHandlerCP;
import laf.core.http.CoreRequestInfo;
import laf.core.http.request.DelegatingHttpRequest;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;

import org.slf4j.Logger;

/**
 * Framework entry point
 */
public class FrontServletBase extends HttpServlet {

	@Inject
	Logger log;

	@Inject
	ConfigurationValue<HttpRequestParserChainCP> parserChain;

	@Inject
	ConfigurationValue<ResourceRequestHandlerCP> resourceRequestHandler;

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

	private void handle(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		try {
			DelegatingHttpRequest request = new DelegatingHttpRequest(req);

			coreRequestInfo.setRequest(request);
			coreRequestInfo.setServletRequest(req);
			coreRequestInfo.setServletResponse(resp);
			coreRequestInfo.setResourceRequestHandler(resourceRequestHandler
					.value().get());

			RequestParseResult<HttpRequest> parseResult = parserChain.value()
					.get().parse(request);
			if (parseResult == null) {
				throw new ServletException("unable to parse path "
						+ request.getPath());
			}
			parseResult.handle(request);
		} catch (Throwable t) {
			log.error(
					"Error while handling request to path " + req.getPathInfo(),
					t);
			throw t;
		}
	}

}
