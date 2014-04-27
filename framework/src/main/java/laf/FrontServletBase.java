package laf;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.controllerInfo.ActionMethodInfo;
import laf.urlMapping.UrlMappingModule;
import laf.urlMapping.parameterValueProvider.ParameterValueProvider;

/**
 * Framework entry point
 */
public class FrontServletBase extends HttpServlet {

	@Inject
	Instance<Object> controllerInstance;

	@Inject
	BeanManager beanManager;

	@Inject
	ActionContext actionContext;

	@Inject
	UrlMappingModule urlMapping;

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		actionContext.setRequest(req);
		actionContext.setResponse(resp);

		// parse request
		ActionPath<ParameterValueProvider> actionPath = urlMapping.parse(req
				.getPathInfo());

		if (actionPath == null) {
			throw new RuntimeException("No Controller found for "
					+ req.getPathInfo());
		}

		// create arguments
		ActionPath<Object> objectActionPath = UrlMappingModule
				.createObjectActionPath(actionPath);
		actionContext.setInvokedPath(objectActionPath);

		// call controller
		ActionResult result = null;
		{
			Object lastActionMethodResult = null;
			for (ActionInvocation<Object> invocation : objectActionPath
					.getElements()) {
				// determine controller
				Object controller;
				ActionMethodInfo methodInfo = invocation.getMethodInfo();
				if (lastActionMethodResult == null) {
					controller = controllerInstance
							.select(methodInfo.getControllerInfo()
									.getControllerClass()).get();
				} else {
					controller = lastActionMethodResult;
				}

				// invoke controller
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

			result = (ActionResult) lastActionMethodResult;
		}

		// render result
		RenderResult renderResult = (RenderResult) result;
		renderResult.sendTo(resp);
	}

}
