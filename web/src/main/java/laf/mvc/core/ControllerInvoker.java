package laf.mvc.core;

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.core.MethodInvocation;
import laf.core.base.ActionResult;
import laf.mvc.core.actionPath.ActionPath;

public class ControllerInvoker implements RequestHandler<Object> {

	@Inject
	@Any
	Instance<Object> controllerInstance;

	@Override
	public ActionResult handle(ActionPath<Object> actionPath) {
		Object lastActionMethodResult = null;
		for (MethodInvocation<Object> invocation : actionPath.getElements()) {
			// determine controller
			Object controller;
			if (lastActionMethodResult == null) {
				Class<?> controllerClass = invocation.getInstanceClass();
				controller = controllerInstance.select(controllerClass).get();
			} else {
				controller = lastActionMethodResult;
			}

			// invoke controller
			try {
				lastActionMethodResult = invocation.getMethod().invoke(
						controller, invocation.getArguments().toArray());
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException("Error calling action method "
						+ invocation.getMethod(), e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(
						"Error during invocation of action method "
								+ invocation.getMethod(), e.getCause());
			}

		}

		return (ActionResult) lastActionMethodResult;
	}

}