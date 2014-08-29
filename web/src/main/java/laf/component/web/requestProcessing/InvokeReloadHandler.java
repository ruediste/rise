package laf.component.web.requestProcessing;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.component.core.Page;
import laf.component.core.RequestHandler;
import laf.component.core.api.CView;
import laf.component.core.pageScope.PageScopeManager;
import laf.component.core.reqestProcessing.PageReloadRequest;
import laf.component.core.tree.Component;
import laf.component.core.tree.ComponentTreeUtil;
import laf.component.web.ApplyValuesUtilImpl;
import laf.component.web.HtmlComponentService;
import laf.component.web.api.CWControllerUtil;
import laf.component.web.template.HtmlTemplateService;
import laf.component.web.template.RaiseEventsUtilImpl;
import laf.core.base.ActionResult;
import laf.core.http.RedirectRenderResult;
import laf.core.http.request.HttpRequest;

import org.slf4j.Logger;

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
			htmlTemplateService.getTemplate(c).applyValues(c, applyValuesUtil);
		}

		// process events
		for (Component c : components) {
			raiseEventsUtil.setComponent(c);
			htmlTemplateService.getTemplate(c).raiseEvents(c, raiseEventsUtil);
		}

		// check if a destination has been defined
		if (controllerUtil.getDestination() != null) {
			return new RedirectRenderResult(null);
		} else {
			// render result
			componentService.renderPage(view, reloadComponent, response);
			return null;
		}
	}

}
