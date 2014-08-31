package laf.component.web.requestProcessing;

import javax.inject.Inject;

import laf.component.core.*;
import laf.component.core.api.CView;
import laf.component.web.HtmlComponentService;
import laf.component.web.api.CWControllerUtil;
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
	Page page;

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
