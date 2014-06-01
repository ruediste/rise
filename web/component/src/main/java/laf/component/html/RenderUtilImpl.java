package laf.component.html;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathFactory;
import laf.actionPath.ActionPathFactory.ActionPathBuilder;
import laf.base.ActionResult;
import laf.component.core.Component;
import laf.component.core.ComponentCoreModule;
import laf.component.html.template.HtmlTemplateService;
import laf.http.request.HttpRequest;
import laf.http.requestMapping.HttpRequestMappingService;

import org.rendersnake.HtmlCanvas;

public class RenderUtilImpl implements RenderUtil {

	@Inject
	ActionPathFactory actionPathFactory;

	@Inject
	HttpRequestMappingService httpRequestMappingService;

	@Inject
	HttpServletRequest request;

	@Inject
	HttpServletResponse response;

	@Inject
	ComponentCoreModule componentCoreModule;

	@Inject
	Instance<RenderUtilImpl> renderUtilInstance;

	@Inject
	HtmlComponentService componentService;

	@Inject
	HtmlTemplateService htmlTemplateService;

	private Component component;

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
		@SuppressWarnings("unchecked")
		HttpRequest url = httpRequestMappingService
				.generate((ActionPath<Object>) path);
		return url(url.getPathWithParameters());
	}

	private String url(String path) {
		String prefix = request.getContextPath();
		prefix += request.getServletPath();
		return response.encodeURL(prefix + "/" + path);
	}

	@Override
	public long pageId() {
		return componentCoreModule.getPageId();
	}

	@Override
	public String getKey(String key) {
		return componentService.calculateKey(component, key);
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	@Override
	public void render(HtmlCanvas html, Component component) throws IOException {
		RenderUtilImpl util = renderUtilInstance.get();
		util.setComponent(component);

		htmlTemplateService.getTemplate(component)
				.render(component, html, util);
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
	public long getComponentId() {
		return componentService.getComponentId(component);
	}
}
