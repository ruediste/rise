package laf.requestProcessing;

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.base.Controller;
import laf.controllerInfo.ActionMethodInfo;

public class DefaultControllerInvoker implements ControllerInvoker {

	@Inject
	@Controller
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
				controller = controllerInstance.select(
						methodInfo.getControllerInfo().getControllerClass())
						.get();
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

			if (lastActionMethodResult == null) {
				throw new RuntimeException("Action method " + methodInfo
						+ " returned null");
			}
		}

		return (ActionResult) lastActionMethodResult;
	}

}