package laf.component.html.impl;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.component.core.ComponentView;
import laf.component.core.Page;
import laf.component.core.impl.ControllerUtilImpl;
import laf.component.html.ApplyValuesUtilImpl;
import laf.component.html.HtmlComponentService;
import laf.component.html.template.HtmlTemplateService;
import laf.component.html.template.RaiseEventsUtilImpl;
import laf.component.pageScope.PageScopeManager;
import laf.component.tree.Component;
import laf.component.tree.ComponentTreeUtil;
import laf.core.actionPath.ActionPath;
import laf.core.http.RedirectRenderResult;
import laf.core.http.request.HttpRequest;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.core.requestProcessing.RequestProcessor;

import org.slf4j.Logger;

public class HtmlInvokeReloadReqestProcessor implements RequestProcessor {
	@Inject
	Logger log;

	@Inject
	PageScopeManager pageScopeManager;

	@Inject
	ApplyValuesUtilImpl applyValuesUtil;

	@Inject
	RaiseEventsUtilImpl raiseEventsUtil;

	@Inject
	Page page;

	@Inject
	HttpRequest request;

	@Inject
	HttpServletResponse response;

	@Inject
	HtmlComponentService componentService;

	@Inject
	HtmlTemplateService htmlTemplateService;

	@Inject
	ControllerUtilImpl controllerUtil;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {

		log.debug("reloading page " + pageScopeManager.getId());

		ComponentView<?> view = page.getView();

		long componentId = Long.parseLong(request.getParameter("componentId"));
		Component reloadComponent = componentService.getComponent(view,
				componentId);

		// apply request values
		List<Component> components = ComponentTreeUtil.subTree(reloadComponent);

		for (Component c : components) {
			applyValuesUtil.setComponent(c);
			htmlTemplateService.getTemplate(c).applyValues(c, applyValuesUtil);
		}

		// process events
		for (Component c : components) {
			raiseEventsUtil.setComponent(c);
			htmlTemplateService.getTemplate(c).raiseEvents(c, raiseEventsUtil);
		}

		// check if a destination has been defined
		if (controllerUtil.getDestination() != null) {
			return new RedirectRenderResult(controllerUtil.getDestination());
		} else {
			// render result
			componentService.renderPage(view, reloadComponent, response);
			return null;
		}
	}

}
