package com.github.ruediste.laf.mvc.web;

import java.io.IOException;

import javax.inject.Inject;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.core.base.LafLogger;
import com.github.ruediste.laf.core.http.*;
import com.github.ruediste.laf.mvc.core.ActionPath;
import com.github.ruediste.laf.mvc.core.DelegatingRequestHandler;

public class ResultRenderer extends DelegatingRequestHandler<String, String> {

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	LafLogger log;

	@Inject
	HttpRenderResultUtil util;

	@Override
	public ActionResult handle(ActionPath<String> path) {

		ActionResult result = getDelegate().handle(path);
		if (result instanceof HttpRenderResult) {
			try {
				((HttpRenderResult) result).sendTo(
						coreRequestInfo.getServletResponse(), util);
			} catch (IOException e) {
				log.error("Error while handling request " + path, e);
			}
		}
		return null;
	}

}
