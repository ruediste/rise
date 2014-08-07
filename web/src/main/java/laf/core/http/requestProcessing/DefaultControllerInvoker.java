package laf.core.http.requestProcessing;

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.ActionResult;
import laf.core.actionPath.ActionInvocation;
import laf.core.actionPath.ActionPath;
import laf.core.controllerInfo.ActionMethodInfo;
import laf.core.controllerInfo.ControllerInfo;
import laf.core.requestProcessing.ControllerInvoker;
import laf.core.requestProcessing.CurrentControllerProducer;

public class DefaultControllerInvoker implements ControllerInvoker {

	@Inject
	@Any
	Instance<Object> controllerInstance;

	@Inject
	CurrentControllerProducer currentControllerProducer;

	@Override
	public ActionResult invoke(ActionPath<Object> actionPath) {
		Object lastActionMethodResult = null;
		for (ActionInvocation<Object> invocation : actionPath.getElements()) {
			// determine controller
			Object controller;
			ActionMethodInfo methodInfo = invocation.getMethodInfo();
			if (lastActionMethodResult == null) {
				ControllerInfo controllerInfo = methodInfo.getControllerInfo();
				Class<?> controllerClass = controllerInfo.getControllerClass();

				controller = controllerInstance.select(controllerClass).get();
			} else {
				controller = lastActionMethodResult;
			}

			// invoke controller
			currentControllerProducer.setCurrentController(controller);
			try {
				lastActionMethodResult = methodInfo.getMethod().invoke(
						controller, invocation.getArguments().toArray());
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException("Error calling action method "
						+ methodInfo, e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(
						"Error during invocation of action method "
								+ methodInfo, e.getCause());
			}

		}

		return (ActionResult) lastActionMethodResult;
	}

}