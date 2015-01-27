package com.github.ruediste.laf.component.web;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.component.core.*;
import com.github.ruediste.laf.component.core.api.CView;
import com.github.ruediste.laf.component.core.pageScope.PageScopeManager;
import com.github.ruediste.laf.component.core.tree.Component;
import com.github.ruediste.laf.component.core.tree.ComponentTreeUtil;
import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.core.http.CoreRequestInfo;
import com.github.ruediste.laf.core.http.RedirectRenderResult;

public class InvokeReloadHandler implements RequestHandler<PageReloadRequest> {
	@Inject
	Logger log;

	@Inject
	PageScopeManager pageScopeManager;

	@Inject
	ApplyValuesUtilImpl applyValuesUtil;

	@Inject
	RaiseEventsUtilImpl raiseEventsUtil;

	@Inject
	PageInfo page;

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	HtmlComponentService componentService;

	@Inject
	TemplateUtil templateUtil;

	@Inject
	CWControllerUtil controllerUtil;

	@Override
	public ActionResult handle(PageReloadRequest request) {

		log.debug("reloading page " + pageScopeManager.getId());

		CView<?> view = page.getView();

		Component reloadComponent = componentService.getComponent(view,
				request.componentNr);

		// apply request values
		List<Component> components = ComponentTreeUtil.subTree(reloadComponent);

		for (Component c : components) {
			applyValuesUtil.setComponent(c);
			templateUtil.getTemplate(c).applyValues(c, applyValuesUtil);
		}

		// process events
		for (Component c : components) {
			raiseEventsUtil.setComponent(c);
			templateUtil.getTemplate(c).raiseEvents(c, raiseEventsUtil);
		}

		// check if a destination has been defined
		if (controllerUtil.getDestinationUrl() != null) {
			return new RedirectRenderResult(controllerUtil.getDestinationUrl());
		} else {
			// render result
			componentService.renderPage(view, reloadComponent,
					coreRequestInfo.getServletResponse());
			return null;
		}
	}

}
