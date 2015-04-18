package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.laf.mvc.ActionInvocation;
import com.github.ruediste.laf.mvc.MvcRequestInfo;
import com.github.ruediste.laf.mvc.PathActionResult;
import com.github.ruediste.laf.util.MethodInvocation;
import com.google.common.base.Joiner;

/**
 * Builder to create {@link ActionInvocation}s for controller method invocations using
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
public abstract class MvcWebActionPathBuilder {

	@Inject
	MvcRequestInfo requestInfo;

	@Inject
	MvcWebControllerReflectionUtil util;

	private PathActionResult path = new PathActionResult();
	private ActionInvocation<Object> currentActionPath;

	/**
	 * Initialize the {@link MvcWebActionPathBuilder} to create an
	 * {@link ActionInvocation}. The current action path is retrieved from the
	 * {@link MvcRequestInfo}.
	 *
	 * @see #initialize(ActionInvocation)
	 */
	public void initialize() {
		currentActionPath = requestInfo.getObjectActionPath();
	}

	/**
	 * Initialize the {@link MvcWebActionPathBuilder} used to create
	 * {@link ActionInvocation}s
	 *
	 * @param currentActionPath
	 *            the {@link ActionInvocation} which is currently being processed.
	 *            Used to determine the full path if a path is started with an
	 *            embedded controller class.
	 */
	public void initialize(ActionInvocation<Object> currentActionPath) {
		this.currentActionPath = currentActionPath;
	}

	/**
	 * Set an {@link AttachedProperty} of the {@link ActionInvocation} to be created.
	 */
	public <T> MvcWebActionPathBuilder set(
			AttachedProperty<ActionInvocation<?>, T> property, T value) {
		property.set(path, value);
		return this;
	}

	/**
	 * An instance of the supplied controllerClass is returned. If the call is
	 * targeted at an embedded controller, the current {@link ActionContext} is
	 * scanned for the last occurence of the supplied controller class, and the
	 * elements of the invoked path up to this point are prepended to the
	 * {@link ActionInvocation} .
	 */
	public <T> T controller(Class<T> controllerClass) {
		return createActionPath(controllerClass, path);
	}

	/**
	 * An instance of the supplied controller class is returned. As soon as an
	 * action method is called on the returned instance, the invoked invocation
	 * is appended to the path.
	 */
	protected @SuppressWarnings("unchecked") <T> T createActionPath(
			final Class<T> controllerClass, final PathActionResult path) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(controllerClass);

		enhancer.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method thisMethod,
					Object[] args, MethodProxy proxy) throws Throwable {

				// add this invocation to the path
				if (!util.isActionMethod(thisMethod)) {
					ArrayList<String> methods = new ArrayList<>();
					for (Method method : controllerClass.getMethods()) {
						if (util.isActionMethod(method)) {
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
				MethodInvocation<Object> invocation = new MethodInvocation<>(
						controllerClass, thisMethod);
				invocation.getArguments().addAll(Arrays.asList(args));
				path.getElements().add(invocation);

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