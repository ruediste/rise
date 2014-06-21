package laf.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import laf.actionPath.PathActionResult;
import laf.base.ActionResult;
import laf.base.InstanceFactory;
import laf.http.request.HttpRequest;
import laf.http.requestMapping.HttpRequestMappingService;

public class RedirectRenderResult implements HttpRenderResult {

	private HttpRequest url;

	public RedirectRenderResult(ActionResult target) {
		HttpRequestMappingService service = InstanceFactory
				.getInstance(HttpRequestMappingService.class);
		url = service.generate((PathActionResult) target);
	}

	@Override
	public void sendTo(HttpServletResponse response, HttpRenderResultUtil util)
			throws IOException {
		response.sendRedirect(util.httpService.redirectUrl(url
				.getPathWithParameters()));
	}
}
