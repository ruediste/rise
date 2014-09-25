package laf.mvc.web;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.core.base.ActionResult;
import laf.core.http.HttpService;
import laf.core.web.resource.ResourceOutput;
import laf.core.web.resource.ResourceRenderUtil;
import laf.mvc.core.PathActionResult;

import org.rendersnake.Renderable;

@RequestScoped
public class RenderUtilImpl implements MWRenderUtil {

	@Inject
	Instance<ActionPathBuilder> actionPathBuilderInstance;

	@Inject
	HttpService httpService;

	@Inject
	RequestMappingUtil mappingUtil;

	@Inject
	ResourceRenderUtil resourceRenderUtil;

	@Override
	public <T> T path(Class<T> controller) {
		return path().controller(controller);
	}

	@Override
	public String url(ActionResult path) {
		return url(mappingUtil.generate((PathActionResult) path));
	}

	@Override
	public String url(String path) {
		return httpService.url(path);
	}

	@Override
	public ActionPathBuilder path() {
		ActionPathBuilder builder = actionPathBuilderInstance.get();
		builder.initialize();
		return builder;
	}

	@Override
	public Renderable jsBundle(ResourceOutput output) {
		return resourceRenderUtil.jsBundle(this::url, output);
	}

	@Override
	public Renderable cssBundle(ResourceOutput output) {
		return resourceRenderUtil.cssBundle(this::url, output);
	}
}
