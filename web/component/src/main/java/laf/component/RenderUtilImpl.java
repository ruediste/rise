package laf.component;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.*;
import laf.actionPath.ActionPathFactory.ActionPathBuilder;
import laf.base.ActionResult;
import laf.httpRequest.HttpRequest;
import laf.httpRequestMapping.HttpRequestMappingService;

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
	ComponentService componentService;

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
		String prefix = request.getContextPath();
		prefix += request.getServletPath();
		return response.encodeURL(prefix + "/" + url.getPathWithParameters());
	}

	@Override
	public RenderUtil forChild(Component child) {
		RenderUtilImpl result = renderUtilInstance.get();
		result.setComponent(child);
		return result;
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

}
