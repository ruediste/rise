package com.github.ruediste.laf.component.core;

import java.lang.reflect.AnnotatedType;

import com.github.ruediste.laf.core.base.Function2;
import com.github.ruediste.laf.core.base.MethodInvocation;
import com.github.ruediste.laf.core.base.attachedProperties.AttachedPropertyBearerBase;

/**
 * An invocation of an action method.
 */
public class ActionInvocation<T> extends AttachedPropertyBearerBase {
	private MethodInvocation<T> invocation;

	public ActionInvocation() {

	}

	public ActionInvocation(MethodInvocation<T> invocation) {
		this.invocation = invocation;
	}

	public MethodInvocation<T> getInvocation() {
		return invocation;
	}

	public void setInvocation(MethodInvocation<T> invocation) {
		this.invocation = invocation;
	}

	public <P> ActionInvocation<P> map(
			Function2<AnnotatedType, ? super T, P> func) {
		MethodInvocation<P> invocation = this.invocation.map(func);
		ActionInvocation<P> result = new ActionInvocation<>(invocation);
		return result;
	}
}
