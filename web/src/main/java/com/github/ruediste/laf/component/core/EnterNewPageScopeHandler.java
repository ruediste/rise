package com.github.ruediste.laf.component.core;

import javax.inject.Inject;

import com.github.ruediste.laf.component.core.*;
import com.github.ruediste.laf.component.core.pageScope.PageScopeManager;
import com.github.ruediste.laf.core.base.ActionResult;

public class EnterNewPageScopeHandler
		extends
		DelegatingRequestHandler<ActionInvocation<String>, ActionInvocation<String>> {

	@Inject
	PageScopeManager mgr;

	@Inject
	PageInfo page;

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
