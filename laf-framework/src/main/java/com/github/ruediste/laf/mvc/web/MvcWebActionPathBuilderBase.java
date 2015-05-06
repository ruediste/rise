package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.laf.core.ControllerReflectionUtil;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.core.actionInvocation.InvocationActionResult;
import com.github.ruediste.laf.mvc.MvcRequestInfo;
import com.github.ruediste.laf.util.MethodInvocation;
import com.google.common.base.Joiner;

/**
 * Builder to create {@link ActionInvocation}s for controller method invocations
 * using a fluent interface. The class must be subclassed by each view
 * technology to allow for extensions. Example:
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
public class MvcWebActionPathBuilderBase<TSelf extends MvcWebActionPathBuilderBase<TSelf>> {

	@Inject
	MvcRequestInfo requestInfo;

	@Inject
	ControllerReflectionUtil util;

	private InvocationActionResult invocation = new InvocationActionResult();

	@SuppressWarnings("unchecked")
	protected TSelf self() {
		return (TSelf) this;
	}

	/**
	 * Set an {@link AttachedProperty} of the {@link ActionInvocation} to be
	 * created.
	 */
	public <T> TSelf set(AttachedProperty<ActionInvocation<?>, T> property,
			T value) {
		property.set(invocation, value);
		return self();
	}

	/**
	 * An instance of the supplied controllerClass is returned.
	 */
	public <T> T go(Class<T> controllerClass) {
		return createActionPath(controllerClass, invocation);
	}

	/**
	 * An instance of the supplied controller class is returned. As soon as an
	 * action method is called on the returned instance, the invoked invocation
	 * is appended to the path.
	 */
	protected @SuppressWarnings("unchecked") <T> T createActionPath(
			final Class<T> controllerClass, final InvocationActionResult path) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(controllerClass);

		enhancer.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method thisMethod,
					Object[] args, MethodProxy proxy) throws Throwable {

				if (!util.isActionMethod(thisMethod)) {
					// collect methods for error message
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

				// create invocation
				MethodInvocation<Object> methodInvocation = new MethodInvocation<>(
						controllerClass, thisMethod);
				methodInvocation.getArguments().addAll(Arrays.asList(args));
				path.methodInvocation = methodInvocation;
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