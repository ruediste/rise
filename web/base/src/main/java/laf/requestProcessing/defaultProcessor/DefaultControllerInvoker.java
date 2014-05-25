package laf.requestProcessing.defaultProcessor;

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.base.*;
import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.ControllerInfo;
import laf.requestProcessing.ControllerInvoker;
import laf.requestProcessing.CurrentControllerProducer;

public class DefaultControllerInvoker implements ControllerInvoker {

	@Inject
	@Controller
	Instance<Object> controllerInstance;

	@Inject
	@EmbeddedController
	Instance<Object> embeddedControllerInstance;

	@Inject
	@ComponentController
	Instance<Object> componentControllerInstance;

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
				switch (controllerInfo.getType()) {
				case COMPONENT:
					controller = componentControllerInstance.select(
							controllerClass).get();
					break;
				case EMBEDDED:
					controller = embeddedControllerInstance.select(
							controllerClass).get();
					break;
				case NORMAL:
					controller = controllerInstance.select(controllerClass)
							.get();
					break;
				default:
					throw new RuntimeException("Should Not Happen");

				}
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