package laf;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import laf.controllerInfo.ActionMethodInfo;
import laf.urlMapping.*;

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
	UrlMapping urlMapping;

	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void initialize() {
		Controller controllerAnnotation = new Controller() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Controller.class;
			}
		};

		for (Bean<?> bean : beanManager.getBeans(Object.class,
				controllerAnnotation)) {
			System.out.println(bean.getBeanClass());
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// parse request
		ActionPath<ParameterValueProvider> actionPath = urlMapping.parse(req
				.getServletPath());

		// call controller
		ActionResult result = null;
		{
			Object lastActionMethodResult = null;
			for (ActionInvocation<ParameterValueProvider> invocation : actionPath
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

				// load arguments
				ArrayList<Object> arguments = new ArrayList<>();
				for (ParameterValueProvider provider : invocation
						.getArguments()) {
					arguments.add(provider.provideValue());
				}

				// invoke controller
				try {
					lastActionMethodResult = methodInfo.getMethod().invoke(
							controller, arguments.toArray());
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
