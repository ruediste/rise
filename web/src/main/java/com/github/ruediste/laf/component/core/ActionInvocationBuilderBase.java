package com.github.ruediste.laf.component.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import net.sf.cglib.proxy.*;

import com.github.ruediste.laf.core.base.MethodInvocation;
import com.google.common.base.Joiner;

public class ActionInvocationBuilderBase {

	@SuppressWarnings("unchecked")
	public <T> T controller(final Class<T> controllerClass) {
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
									+ " while generating an ActionInvocation. Available Methods:\n"
									+ Joiner.on("\n").join(methods));
				}
				MethodInvocation<Object> invocation = new MethodInvocation<>(
						controllerClass, thisMethod);
				invocation.getArguments().addAll(Arrays.asList(args));

				return createActionInvocation(invocation);
			}
		});

		try {
			return (T) enhancer.create();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PathActionInvocation createActionInvocation(
			MethodInvocation<Object> invocation) {
		return new PathActionInvocation(invocation);
	}
}
