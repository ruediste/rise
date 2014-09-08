package laf.component.web;

import javax.inject.Inject;

import laf.component.core.*;
import laf.component.core.api.CView;
import laf.core.base.ActionResult;
import laf.core.http.CoreRequestInfo;
import laf.core.http.RedirectRenderResult;

public class RenderInitialPageHandler
		extends
		DelegatingRequestHandler<ActionInvocation<Object>, ActionInvocation<Object>> {

	@Inject
	CWControllerUtil controllerUtil;

	@Inject
	HtmlComponentService componentService;

	@Inject
	ComponentViewRepository viewRepository;

	@Inject
	PageInfo page;

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Override
	public ActionResult handle(ActionInvocation<Object> invocation) {
		getDelegate().handle(invocation);

		// check if a destination has been defined
		if (controllerUtil.getDestinationUrl() != null) {
			return new RedirectRenderResult(controllerUtil.getDestinationUrl());
		} else {

			CView<Object> view = viewRepository
					.createView(page.getController());

			page.setView(view);

			// render result
			componentService.renderPage(view, view.getRootComponent(),
					coreRequestInfo.getServletResponse());
			return null;
		}
	}

}
