package laf.component.core;

import java.lang.reflect.Type;

import laf.core.base.Function2;
import laf.core.base.MethodInvocation;
import laf.core.base.attachedProperties.AttachedPropertyBearerBase;

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

	public <P> ActionInvocation<P> map(Function2<Type, ? super T, P> func) {
		MethodInvocation<P> invocation = this.invocation.map(func);
		ActionInvocation<P> result = new ActionInvocation<>(invocation);
		return result;
	}
}
