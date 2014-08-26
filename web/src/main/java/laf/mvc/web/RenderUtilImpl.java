package laf.mvc.web;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.ActionResult;
import laf.core.http.HttpService;
import laf.mvc.actionPath.PathActionResult;

import org.rendersnake.HtmlCanvas;

@RequestScoped
public class RenderUtilImpl implements RenderUtil {

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

	@Override
	public String resourceUrl(String string) {
		return httpService.resourceUrl(string);
	}

	@Override
	public void startHtmlPage(HtmlCanvas html) throws IOException {
		httpService.startHtmlPage(html);
	}

}
