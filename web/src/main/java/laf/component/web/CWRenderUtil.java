package laf.component.web;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import laf.component.core.pageScope.PageScopeManager;
import laf.component.core.tree.Component;
import laf.core.web.resource.ResourceRenderUtil;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

@ApplicationScoped
public class CWRenderUtil extends PathGeneratingUtilImpl {

	@Inject
	HtmlComponentService componentService;

	@Inject
	TemplateUtil templateUtil;

	@Inject
	PageScopeManager pageScopeManager;

	@Inject
	WebRequestInfo webRequestInfo;

	@Inject
	ResourceRenderUtil resourceRenderUtil;

	private ThreadLocal<Component> currentComponent = new ThreadLocal<Component>();

	public long pageId() {
		return pageScopeManager.getId();
	}

	public String getKey(String key) {
		return componentService.calculateKey(getComponent(), key);
	}

	public Component getComponent() {
		Component result = currentComponent.get();
		if (result == null) {
			throw new RuntimeException(
					"Current Component not set. Is CWRenderUtil used outside of the render method of a template?");
		}
		return result;
	}

	public void render(HtmlCanvas html, Component component) throws IOException {
		Component old = currentComponent.get();
		try {
			currentComponent.set(component);
			templateUtil.getTemplate(component).render(component, html);
		} finally {
			currentComponent.set(old);
		}

	}

	public long getComponentId() {
		return componentService.getComponentId(getComponent());
	}

	public long getPageId() {
		return pageScopeManager.getId();
	}

	public String getReloadUrl() {
		return url(webRequestInfo.getReloadPath() + "/" + getPageId());
	}

	public Renderable jsBundle(String... files) {
		return resourceRenderUtil.jsBundle(this::url, files);
	}

	public Renderable cssBundle(String... files) {
		return resourceRenderUtil.cssBundle(this::url, files);
	}

}
