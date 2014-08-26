package laf.core.html;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.core.http.HttpService;
import laf.core.http.requestMapping.HttpRequestMappingService;
import laf.mvc.actionPath.ActionPathBuilderBase;
import laf.mvc.actionPath.ActionPathFactory;

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
	public ActionPathBuilderBase path() {
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
