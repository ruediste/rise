package laf.http;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;

public class RedirectRenderResult implements HttpRenderResult {

	@Inject
	HttpService httpService;

	ActionResult target;

	public RedirectRenderResult(ActionResult target) {
		this.target = target;
	}

	@Override
	public void sendTo(HttpServletResponse response, HttpRenderResultUtil util)
			throws IOException {
		response.sendRedirect(util.httpService.redirectUrl(target));
	}
}
