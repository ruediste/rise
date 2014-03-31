package laf.urlMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.ejb.Singleton;
import javax.inject.Inject;

import laf.ActionContext;
import laf.controllerInfo.*;
import net.sf.cglib.proxy.*;

import com.google.common.base.Joiner;

@Singleton
public class ActionPathFactory {

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	@Inject
	ActionContext actionContext;

	/**
	 * Method to create {@link ActionPath}s. An instance of the supplied
	 * controllerClass is returned. As soon as an action method is called on the
	 * returned instance, the {@link ActionPathProcessor} is invoked with the
	 * {@link ActionPath} corresponding to the invoked action method.
	 */
	public <T> T createActionPath(final Class<T> controllerClass) {
		ControllerInfo controllerInfo = controllerInfoRepository
				.getControllerInfo(controllerClass);
		if (controllerInfo.isEmbeddedController()) {
			// look for the latest occurrence of the controller class
			// in the invoked path, and use the prefix for the to be generated
			// path
			actionContext.getInvokedPath().getElements();
		}
		return createActionPath(controllerClass, new PathActionResult());
	}

	/**
	 * An instance of the supplied controller class is returned. As soon as an
	 * action method is called on the returned instance, the invoked invocation
	 * is appended to the path. When the final action method is called, the
	 * supplied processor is invoked with the whole action path as argument.
	 */
	@SuppressWarnings("unchecked")
	private <T> T createActionPath(final Class<T> controllerClass,
			final PathActionResult path) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(controllerClass);

		enhancer.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method thisMethod,
					Object[] args, MethodProxy proxy) throws Throwable {

				// add this invocation to the path
				ActionInvocation<Object> invocation = new ActionInvocation<>();
				ControllerInfo controllerInfo = controllerInfoRepository
						.getControllerInfo(controllerClass);
				ActionMethodInfo methodInfo = controllerInfo
						.getActionMethodInfo(thisMethod);
				if (methodInfo == null) {
					throw new RuntimeException(
							"The method "
									+ thisMethod.getName()
									+ " wich is not action method has been called on a controller of type "
									+ controllerClass.getName()
									+ " while generating an ActionPath. Available Methods:\n"
									+ Joiner.on("\n")
									.join(controllerInfo
											.getActionMethodInfos()
											.stream()
											.map(i -> i.getSignature())
											.collect(
													Collectors.toList())));
				}
				invocation.setMethodInfo(methodInfo);
				invocation.getArguments().addAll(Arrays.asList(args));
				path.getElements().add(invocation);

				// check for embedded controllers
				if (methodInfo.returnsEmbeddedController()) {
					// find the
					// add another element to the invocation path
					return createActionPath(thisMethod.getReturnType(), path);
				}

				return path;
			}
		});

		try {
			return (T) enhancer.create();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
