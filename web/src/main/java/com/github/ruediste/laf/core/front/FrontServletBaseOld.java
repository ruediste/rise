package com.github.ruediste.laf.core.front;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.base.configuration.ConfigurationValue;
import com.github.ruediste.laf.core.defaultConfiguration.HttpRequestParserChainCP;
import com.github.ruediste.laf.core.defaultConfiguration.ResourceRequestHandlerCP;
import com.github.ruediste.laf.core.http.CoreRequestInfo;
import com.github.ruediste.laf.core.http.request.DelegatingHttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParseResult;

/**
 * Framework entry point
 */
public class FrontServletBaseOld extends HttpServlet {

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

			log.debug("received request " + request.getPathInfo());

			coreRequestInfo.setRequest(request);
			coreRequestInfo.setServletRequest(req);
			coreRequestInfo.setServletResponse(resp);

			RequestParseResult parseResult = parserChain.value().get()
					.parse(request);
			if (parseResult == null) {
				throw new ServletException("unable to parse path "
						+ request.getPathInfo());
			}
			parseResult.handle();
		} catch (Throwable t) {
			log.error(
					"Error while handling request to path " + req.getPathInfo(),
					t);
			throw t;
		}
	}

}
