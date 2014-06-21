package laf.html;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.ActionPathFactory;
import laf.actionPath.ActionPathFactory.ActionPathBuilder;
import laf.base.ActionResult;
import laf.http.HttpService;
import laf.http.requestMapping.HttpRequestMappingService;

public class RenderUtilBaseImpl implements RenderUtilBase {

	@Inject
	ActionPathFactory actionPathFactory;

	@Inject
	HttpRequestMappingService httpRequestMappingService;

	@Inject
	HttpServletRequest request;

	@Inject
	HttpServletResponse response;

	@Inject
	Instance<RenderUtilBaseImpl> renderUtilInstance;

	@Inject
	HttpService httpService;

	@Override
	public <T> T path(Class<T> controller) {
		return path().controller(controller);
	}

	@Override
	public ActionPathBuilder path() {
		return actionPathFactory.buildActionPath();
	}

	@Override
	public String url(ActionResult path) {
		return httpService.url(path);
	}

	/**
	 * Return the URL of a resource
	 */
	@Override
	public String resourceUrl(String resource) {
		return response.encodeURL(request.getContextPath() + "/static/"
				+ resource);
	}
}
