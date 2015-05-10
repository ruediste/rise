package com.github.ruediste.rise.mvc;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ControllerInvokerBase;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcControllerInvoker extends ControllerInvokerBase {
	@Inject
	Injector injector;

	@Override
	protected Object getController(
			ActionInvocation<String> stringActionInvocation) {
		Object controller = injector
				.getInstance(stringActionInvocation.methodInvocation
						.getInstanceClass());
		return controller;
	}

}