package laf.mvc.actionPath;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import laf.base.attachedProperties.AttachedProperty;
import net.sf.cglib.proxy.*;

import com.google.common.base.Joiner;

/**
 * Builder to create {@link ActionPath}s for controller method invocations using
 * a fluent interface. The class must be subclassed by each view technology to
 * allow for extendsions. Example:
 * 
 * <pre>
 * <code>
 * ActionPathBuilder builder=instance.get();
 * builder.initialize();
 * ActionPath path= builder.
 * 			.set(property, 27)
 * 			.controller(TestController.class).actionMethod(5);
 * </code>
 * </pre>
 * 
 */
public abstract class ActionPathBuilderBase {

	@Inject
	ActionPath<Object> injectedCurrentActionPath;

	private PathActionResult path = new PathActionResult();
	private ActionPath<Object> currentActionPath;

	/**
	 * Initialize the {@link ActionPathBuilderBase} to create an
	 * {@link ActionPath}. The current action path is beeing injected.
	 * 
	 * @see #initialize(ActionPath)
	 */
	public void initialize() {
		currentActionPath = injectedCurrentActionPath;
	}

	/**
	 * Initialize the {@link ActionPathBuilderBase} used to create
	 * {@link ActionPath}s
	 * 
	 * @param currentActionPath
	 *            the {@link ActionPath} which is currently being processed.
	 *            Used to determine the full path if a path is started with an
	 *            embedded controller class.
	 */
	public void initialize(ActionPath<Object> currentActionPath) {
		this.currentActionPath = currentActionPath;
	}

	/**
	 * Set an {@link AttachedProperty} of the {@link ActionPath} to be created.
	 */
	public <T> ActionPathBuilderBase set(
			AttachedProperty<ActionPath<?>, T> property, T value) {
		property.set(path, value);
		return this;
	}

	/**
	 * An instance of the supplied controllerClass is returned. If the call is
	 * targeted at an embedded controller, the current {@link ActionContext} is
	 * scanned for the last occurence of the supplied controller class, and the
	 * elements of the invoked path up to this point are prepended to the
	 * {@link ActionPath} .
	 */
	public <T> T controller(Class<T> controllerClass) {

		if (ControllerReflectionUtil.isEmbeddedController(controllerClass)) {
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
						.getControllerClass())) {
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

	/**
	 * An instance of the supplied controller class is returned. As soon as an
	 * action method is called on the returned instance, the invoked invocation
	 * is appended to the path.
	 */
	protected @SuppressWarnings("unchecked")
	<T> T createActionPath(final Class<T> controllerClass,
			final PathActionResult path) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(controllerClass);

		enhancer.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method thisMethod,
					Object[] args, MethodProxy proxy) throws Throwable {

				// add this invocation to the path
				if (!ControllerReflectionUtil.isActionMethod(thisMethod)) {
					ArrayList<String> methods = new ArrayList<>();
					for (Method method : controllerClass.getMethods()) {
						if (ControllerReflectionUtil.isActionMethod(method)) {
							methods.add(method.toString());
						}
					}
					throw new RuntimeException(
							"The method "
									+ thisMethod.getName()
									+ " wich is no action method has been called on a controller of type "
									+ controllerClass.getName()
									+ " while generating an ActionPath. Available Methods:\n"
									+ Joiner.on("\n").join(methods));
				}
				ActionInvocation<Object> invocation = new ActionInvocation<>(
						controllerClass, thisMethod);
				invocation.getArguments().addAll(Arrays.asList(args));
				path.getElements().add(invocation);

				// check for embedded controllers
				if (ControllerReflectionUtil.isEmbeddedController(thisMethod
						.getReturnType())) {
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