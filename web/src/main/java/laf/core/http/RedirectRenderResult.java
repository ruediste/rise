package laf.core.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.InstanceFactory;
import laf.core.http.request.HttpRequest;
import laf.core.http.requestMapping.HttpRequestMappingService;
import laf.mvc.actionPath.PathActionResult;

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
