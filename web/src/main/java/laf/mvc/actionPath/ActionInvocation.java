package laf.mvc.actionPath;

import java.lang.reflect.Method;
import java.util.*;

import laf.mvc.actionPath.ActionPath.ParameterValueComparator;

import com.google.common.base.Objects;

/**
 * An invocation of an action method.
 */
public class ActionInvocation<T> {
	private final List<T> arguments = new ArrayList<>();
	final private Class<?> controllerClass;
	final private Method method;

	public ActionInvocation(Class<?> controllerClass, Method method) {
		this.controllerClass = controllerClass;
		this.method = method;

	}

	/**
	 * Create a copy of this invocation, not including the argument list
	 */
	public ActionInvocation(ActionInvocation<?> invocation) {
		controllerClass = invocation.controllerClass;
		method = invocation.method;
	}

	public List<T> getArguments() {
		return arguments;
	}

	public <O> boolean isCallToSameActionMethod(ActionInvocation<O> other,
			ParameterValueComparator<? super T, ? super O> comparator) {
		if (method != other.method) {
			return false;
		}
		if (arguments.size() != other.getArguments().size()) {
			return false;
		}

		Iterator<T> it = arguments.iterator();
		Iterator<O> oit = other.getArguments().iterator();
		while (it.hasNext() && oit.hasNext()) {
			if (!comparator.equals(it.next(), oit.next())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("method", method)
				.add("arguments", arguments).toString();
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getControllerClass() {
		return controllerClass;
	}

}
