package laf.mvc.web;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.core.base.ActionResult;
import laf.core.http.HttpService;
import laf.mvc.core.actionPath.PathActionResult;
import laf.mvc.web.api.MWRenderUtil;

@RequestScoped
public class RenderUtilImpl implements MWRenderUtil {

	@Inject
	Instance<ActionPathBuilder> actionPathBuilderInstance;

	@Inject
	HttpService httpService;

	@Inject
	RequestMappingUtil mappingUtil;

	@Override
	public <T> T path(Class<T> controller) {
		return path().controller(controller);
	}

	@Override
	public String url(ActionResult path) {
		return httpService.url(mappingUtil.generate((PathActionResult) path));
	}

	@Override
	public ActionPathBuilder path() {
		ActionPathBuilder builder = actionPathBuilderInstance.get();
		builder.initialize();
		return builder;
	}

}
