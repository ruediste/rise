package laf.mvc.web;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.LafLogger;
import laf.core.http.HttpRenderResult;
import laf.mvc.DelegatingRequestHandler;
import laf.mvc.actionPath.ActionPath;

public class ResultRenderer extends DelegatingRequestHandler<String, String> {

	@Inject
	HttpServletResponse response;

	@Inject
	LafLogger log;

	@Override
	public ActionResult handle(ActionPath<String> path) {

		ActionResult result = getDelegate().handle(path);
		if (result instanceof HttpRenderResult) {
			try {
				((HttpRenderResult) result).sendTo(response, null);
			} catch (IOException e) {
				log.error("Error while handling request " + path, e);
			}
		}
		return null;
	}

}
