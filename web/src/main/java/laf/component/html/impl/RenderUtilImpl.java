package laf.component.html.impl;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.actionPath.PathActionResult;
import laf.component.core.ComponentConstants;
import laf.component.core.ComponentCoreModule;
import laf.component.html.HtmlComponentService;
import laf.component.html.RenderUtil;
import laf.component.html.template.HtmlTemplateService;
import laf.component.pageScope.PageScopeManager;
import laf.component.tree.Component;
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

	@Inject
	PageScopeManager pageScopeManager;

	@Inject
	ActionPath<Object> currentActionPath;

	private Component component;

	@Override
	public long pageId() {
		return pageScopeManager.getId();
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

	@Override
	public PathActionResult getReloadPath() {
		PathActionResult result = new PathActionResult();
		ActionInvocation<Object> invocation = new ActionInvocation<>();
		invocation.setMethodInfo(currentActionPath.getFirst()
				.getControllerInfo()
				.getActionMethodInfo(ComponentConstants.reloadMethodName));
		invocation.getArguments().add(pageId());
		result.getElements().add(invocation);
		return result;
	}
}
