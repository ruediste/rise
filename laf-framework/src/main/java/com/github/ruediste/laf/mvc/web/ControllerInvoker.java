package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.web.HttpRenderResult;
import com.github.ruediste.laf.mvc.ActionInvocation;
import com.github.ruediste.salta.jsr330.Injector;

public class ControllerInvoker implements Runnable {

	@Inject
	MvcWebRequestInfo info;

	@Inject
	ActionInvocationUtil util;

	@Inject
	Injector injector;

	@Override
	public void run() {
		ActionInvocation<Object> objectInvocation = util
				.toObjectInvocation(info.getStringActionInvocation());

		// instantiate controller
		Object controller = injector
				.getInstance(objectInvocation.controllerClass);

		// invoke controller
		try {
			ActionResult result = (ActionResult) objectInvocation.methodInvocation
					.getMethod().invoke(
							controller,
							objectInvocation.methodInvocation.getArguments()
									.toArray());
			info.setActionResult((HttpRenderResult) result);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException("Error calling action method "
					+ objectInvocation.methodInvocation.getMethod(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(
					"Error during invocation of action method "
							+ objectInvocation.methodInvocation.getMethod(),
					e.getCause());
		}

	}

}