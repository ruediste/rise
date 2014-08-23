package laf.mvc;

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.ActionResult;
import laf.core.requestProcessing.CurrentControllerProducer;
import laf.mvc.actionPath.ActionInvocation;
import laf.mvc.actionPath.ActionPath;

public class ControllerInvoker implements RequestHandler<Object> {

	@Inject
	@Any
	Instance<Object> controllerInstance;

	@Inject
	CurrentControllerProducer currentControllerProducer;

	@Override
	public ActionResult handle(ActionPath<Object> actionPath) {
		Object lastActionMethodResult = null;
		for (ActionInvocation<Object> invocation : actionPath.getElements()) {
			// determine controller
			Object controller;
			if (lastActionMethodResult == null) {
				Class<?> controllerClass = invocation.getControllerClass();
				controller = controllerInstance.select(controllerClass).get();
			} else {
				controller = lastActionMethodResult;
			}

			// invoke controller
			currentControllerProducer.setCurrentController(controller);
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