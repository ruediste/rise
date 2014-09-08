package laf.component.web;

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.core.*;
import laf.component.core.api.CController;
import laf.core.base.ActionResult;
import laf.core.base.MethodInvocation;

public class InvokeInitialHandler implements
		RequestHandler<ActionInvocation<Object>> {

	@CController
	@Inject
	Instance<Object> controllerInstance;

	@Inject
	PageInfo page;

	@Override
	public ActionResult handle(ActionInvocation<Object> request) {
		MethodInvocation<Object> invocation = request.getInvocation();
		Object controller = controllerInstance.select(
				invocation.getInstanceClass()).get();
		page.setController(controller);
		try {
			return (ActionResult) invocation.getMethod().invoke(controller,
					invocation.getArguments().toArray());
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(
					"Error while invoking controller method", e);

		} catch (InvocationTargetException e) {
			throw new RuntimeException("Error in controller method",
					e.getCause());
		}
	}

}
