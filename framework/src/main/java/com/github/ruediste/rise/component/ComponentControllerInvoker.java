package com.github.ruediste.rise.component;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ControllerInvokerBase;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;

public class ComponentControllerInvoker extends ControllerInvokerBase {

	@Inject
	PageInfo pageInfo;

	@Override
	protected Object getController(
			ActionInvocation<String> stringActionInvocation) {
		return pageInfo.getController();
	}

}
