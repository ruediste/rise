package laf.actionPath;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import laf.attachedProperties.AttachedProperty;
import laf.base.ActionContext;
import laf.controllerInfo.*;
import net.sf.cglib.proxy.*;

import com.google.common.base.Joiner;

/**
 * Factory to create {@link ActionPath}s for controller method invocations using
 * a fluent interface. Example:
 *
 * <pre>
 * <code>
 * ActionPath path= (ActionPath) factory
 * 			.buildActionPath()
 * 			.set(property, 27)
 * 			.controller(TestController.class).actionMethod(5);
 * </code>
 * </pre>
 *
 */
@ApplicationScoped
public class ActionPathFactory {

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	@Inject
	ActionPath<Object> injectedCurrentActionPath;

	public class ActionPathBuilder {
		private PathActionResult path = new PathActionResult();
		private ActionPath<Object> currentActionPath;

		public ActionPathBuilder(ActionPath<Object> currentActionPath) {
			this.currentActionPath = currentActionPath;
		}

		/**
		 * Set an {@link AttachedProperty} of the {@link ActionPath} to be
		 * created.
		 */
		public <T> ActionPathBuilder set(
				AttachedProperty<ActionPath<?>, T> property, T value) {
			property.set(path, value);
			return this;
		}

		/**
		 * An instance of the supplied controllerClass is returned. If the call
		 * is targeted at an embedded controller, the current
		 * {@link ActionContext#getInvokedPath()} is scanned for the last
		 * occurence of the supplied controller class, and the elements of the
		 * invoked path up to this point are prepended to the {@link ActionPath}
		 * .
		 */
		public <T> T controller(Class<T> controllerClass) {
			ControllerInfo controllerInfo = controllerInfoRepository
					.getControllerInfo(controllerClass);
			if (controllerInfo == null) {
				throw new RuntimeException(
						"Could not find controllerInfo for the provided controller class "
								+ controllerClass.getName());
			}

			if (controllerInfo.getType() == ControllerType.EMBEDDED) {
				// look for the latest occurrence of the controller class
				// in the invoked path, and use the prefix for the to be
				// generated
				// path

				if (currentActionPath == null) {
					throw new RuntimeException(
							"Cannot generate an ActionPath starting with empeddedController "
									+ controllerClass.getName()
									+ " since no current action path was specified.");
				}
				ArrayList<ActionInvocation<Object>> elements = currentActionPath
						.getElements();
				boolean found = false;
				for (int i = elements.size() - 1; i >= 0; i--) {
					if (controllerClass.isAssignableFrom(elements.get(i)
							.getControllerInfo().getControllerClass())) {
						for (int p = 0; p < i; p++) {
							path.getElements().add(elements.get(p));
						}
						found = true;
						break;
					}
				}

				if (!found) {
					throw new RuntimeException(
							"Attempted to generate an ActionPath starting with controller "
									+ controllerClass.getName()
									+ ", but did not find a suiting stating point within invoked ActionPath "
									+ currentActionPath);
				}
			}
			return createActionPath(controllerClass, path);
		}
	}

	/**
	 * Create a new {@link ActionPathBuilder} used to create {@link ActionPath}s
	 *
	 * @param currentActionPath
	 *            the {@link ActionPath} which is currently being processed.
	 *            Used to determine the full path if a path is started with an
	 *            embedded controller class.
	 */
	public ActionPathBuilder buildActionPath(
			ActionPath<Object> currentActionPath) {
		return new ActionPathBuilder(currentActionPath);
	}

	/**
	 * Create a new {@link ActionPathBuilder} to create an {@link ActionPath}.
	 * The current action path is beeing injected.
	 * 
	 * @see #buildActionPath(ActionPath)
	 */
	public ActionPathBuilder buildActionPath() {
		return new ActionPathBuilder(injectedCurrentActionPath);
	}

	/**
	 * An instance of the supplied controller class is returned. As soon as an
	 * action method is called on the returned instance, the invoked invocation
	 * is appended to the path.
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
					ArrayList<String> methods = new ArrayList<>();
					for (ActionMethodInfo method : controllerInfo
							.getActionMethodInfos()) {
						methods.add(method.getSignature());
					}
					throw new RuntimeException(
							"The method "
									+ thisMethod.getName()
									+ " wich is no action method has been called on a controller of type "
									+ controllerClass.getName()
									+ " while generating an ActionPath. Available Methods:\n"
									+ Joiner.on("\n").join(methods));
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
