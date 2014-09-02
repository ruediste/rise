package laf.component.web;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.pageScope.PageScopeManager;
import laf.component.core.tree.Component;

import org.rendersnake.HtmlCanvas;

public class CWRenderUtil extends PathGeneratingUtilImpl {

	@Inject
	HtmlComponentService componentService;

	@Inject
	HtmlTemplateService htmlTemplateService;

	@Inject
	PageScopeManager pageScopeManager;

	private ThreadLocal<Component> currentComponent = new ThreadLocal<Component>();

	public long pageId() {
		return pageScopeManager.getId();
	}

	public String getKey(String key) {
		return componentService.calculateKey(getComponent(), key);
	}

	public Component getComponent() {
		return currentComponent.get();
	}

	public void render(HtmlCanvas html, Component component) throws IOException {
		Component old = currentComponent.get();
		try {
			currentComponent.set(component);
			htmlTemplateService.getTemplate(component).render(component, html);
		} finally {
			currentComponent.set(old);
		}

	}

	public long getComponentId() {
		return componentService.getComponentId(getComponent());
	}

	public String getReloadPath() {
		throw new UnsupportedOperationException();
	}
}
