package laf.component;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.*;
import laf.actionPath.ActionPathFactory.ActionPathBuilder;
import laf.base.ActionResult;
import laf.httpRequest.HttpRequest;
import laf.httpRequestMapping.HttpRequestMappingService;

@ApplicationScoped
public class ComponentUtil {
	@Inject
	ActionPathFactory actionPathFactory;

	@Inject
	HttpRequestMappingService httpRequestMappingService;

	@Inject
	HttpServletResponse response;

	@Inject
	ComponentCoreModule componentCoreModule;

	private static ComponentUtil instance;

	@PostConstruct
	public void initialize() {
		instance = this;
	}

	public <T> T path(Class<T> controller) {
		return path().controller(controller);
	}

	public ActionPathBuilder path() {
		return actionPathFactory.buildActionPath();
	}

	public String url(ActionResult path) {
		@SuppressWarnings("unchecked")
		HttpRequest url = httpRequestMappingService
		.generate((ActionPath<Object>) path);
		return response.encodeURL(url.getPathWithParameters());
	}

	public long pageId() {
		return componentCoreModule.getPageId();
	}

	public static ComponentUtil getInstance() {
		return instance;
	}
}
