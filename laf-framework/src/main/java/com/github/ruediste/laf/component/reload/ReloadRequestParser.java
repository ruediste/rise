package com.github.ruediste.laf.component.reload;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.component.ComponentConfiguration;
import com.github.ruediste.laf.component.ComponentRequestInfo;
import com.github.ruediste.laf.core.RequestParseResult;
import com.github.ruediste.laf.core.RequestParser;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;

public class ReloadRequestParser implements RequestParser {

	@Inject
	Logger log;
	@Inject
	ComponentConfiguration config;

	@Inject
	PageReloadRequest request;

	@Inject
	ComponentRequestInfo componentRequestInfo;

	public class ReloadParseResult implements RequestParseResult {

		@Override
		public void handle() {
			componentRequestInfo.setComponentRequest(true);
			config.handleReloadRequest();
		}

	}

	@Override
	public RequestParseResult parse(HttpRequest req) {
		try {
			request.setPageNr(Long.parseLong(req.getParameter("page")));
		} catch (Throwable t) {
			log.warn("no page number parameter sent along with reload request");
			throw t;
		}
		try {
			request.setComponentNr(Long.parseLong(req.getParameter("nr")));
		} catch (Throwable t) {
			log.warn("no component nr parameter sent along with reload request");
			throw t;
		}
		return new ReloadParseResult();
	}
}
