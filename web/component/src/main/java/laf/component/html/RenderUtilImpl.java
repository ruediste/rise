package laf.component.html;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.core.Component;
import laf.component.core.ComponentCoreModule;
import laf.component.html.template.HtmlTemplateService;
import laf.html.RenderUtilBaseImpl;

import org.rendersnake.HtmlCanvas;

public class RenderUtilImpl extends RenderUtilBaseImpl implements RenderUtil {

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

	@Override
	public long getComponentId() {
		return componentService.getComponentId(component);
	}
}
