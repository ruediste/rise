package laf.component.web.api;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.core.ComponentCoreModule;
import laf.component.core.pageScope.PageScopeManager;
import laf.component.core.tree.Component;
import laf.component.web.HtmlComponentService;
import laf.component.web.PathGeneratingUtilImpl;
import laf.component.web.template.HtmlTemplateService;

import org.rendersnake.HtmlCanvas;

public class CWRenderUtil extends PathGeneratingUtilImpl {

	@Inject
	ComponentCoreModule componentCoreModule;

	@Inject
	Instance<CWRenderUtil> renderUtilInstance;

	@Inject
	HtmlComponentService componentService;

	@Inject
	HtmlTemplateService htmlTemplateService;

	@Inject
	PageScopeManager pageScopeManager;

	private Component component;

	public long pageId() {
		return pageScopeManager.getId();
	}

	public String getKey(String key) {
		return componentService.calculateKey(component, key);
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public void render(HtmlCanvas html, Component component) throws IOException {
		CWRenderUtil util = renderUtilInstance.get();
		util.setComponent(component);

		htmlTemplateService.getTemplate(component)
				.render(component, html, util);
	}

	public long getComponentId() {
		return componentService.getComponentId(component);
	}

	public String getReloadPath() {
		throw new UnsupportedOperationException();
	}
}
