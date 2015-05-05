package com.github.ruediste.laf.mvc.web;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ControllerInvokerBase;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcControllerInvoker extends ControllerInvokerBase {
	@Inject
	Injector injector;

	@Override
	protected Object getController(
			ActionInvocation<String> stringActionInvocation) {
		Object controller = injector
				.getInstance(stringActionInvocation.controllerClass);
		return controller;
	}

}