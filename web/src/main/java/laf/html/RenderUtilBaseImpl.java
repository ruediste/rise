package laf.html;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.ActionPathFactory;
import laf.actionPath.ActionPathFactory.ActionPathBuilder;
import laf.base.ActionResult;
import laf.http.HttpService;
import laf.http.requestMapping.HttpRequestMappingService;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

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

	@Override
	public void startHtmlPage(HtmlCanvas html) throws IOException {
		html.write(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">",
				false);
		html.html(HtmlAttributesFactory.xmlns("http://www.w3.org/1999/xhtml"));
	}
}
