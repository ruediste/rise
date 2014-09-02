package laf.component.core;

import javax.inject.Inject;

import laf.component.core.*;
import laf.component.core.pageScope.PageScopeManager;
import laf.core.base.ActionResult;

public class EnterNewPageScopeHandler
		extends
		DelegatingRequestHandler<ActionInvocation<String>, ActionInvocation<String>> {

	@Inject
	PageScopeManager mgr;

	@Inject
	Page page;

	@Override
	public ActionResult handle(ActionInvocation<String> request) {
		mgr.enterNew();
		try {
			return getDelegate().handle(request);
		} finally {
			mgr.leave();
		}
	}

}
