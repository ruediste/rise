package com.github.ruediste.laf.component;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ControllerInvokerBase;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;

public class ComponentControllerInvoker extends ControllerInvokerBase {

	@Inject
	PageInfo pageInfo;

	@Override
	protected Object getController(
			ActionInvocation<String> stringActionInvocation) {
		return pageInfo.getController();
	}

}
