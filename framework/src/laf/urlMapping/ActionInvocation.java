package laf.urlMapping;

import java.util.*;

import laf.controllerInfo.ActionMethodInfo;
import laf.urlMapping.ActionPath.ParameterValueComparator;

/**
 * An invocation of an action method.
 */
public class ActionInvocation<T> {
	private final List<T> arguments = new ArrayList<>();
	private ActionMethodInfo methodInfo;

	public ActionInvocation() {

	}

	/**
	 * Create a copy of this invocation, not including the argument list
	 */
	public ActionInvocation(ActionInvocation<?> invocation) {
		this.methodInfo = invocation.methodInfo;
	}

	public List<T> getArguments() {
		return arguments;
	}

	public ActionMethodInfo getMethodInfo() {
		return methodInfo;
	}

	public void setMethodInfo(ActionMethodInfo methodInfo) {
		this.methodInfo = methodInfo;
	}

	public <O> boolean isCallToSameActionMethod(ActionInvocation<O> other,
			ParameterValueComparator<? super T, ? super O> comparator) {
		if (methodInfo != other.getMethodInfo()) {
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

	/*private ActionMethodMappingEntry method;

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("method", method)
				.add("args", arguments).toString();
	}

	public ActionMethodMappingEntry getMethod() {
		return method;
	}

	public void setMethod(ActionMethodMappingEntry method) {
		this.method = method;
	}

	/ **
	 * Check if the number of arguments and the number of parameters of the
	 * action method matches
	 * /
	public void checkArgumentCount() {
		if (arguments.size() != getMethod().parameters.size()) {
			throw new DispatchError(
					"The number of provided arguments does not match the number of"
							+ "expected parameters of the action method. Action method: "
							+ getMethod().method + " Arguments: "
							+ Joiner.on(",").join(arguments));
		}
	}

	public List<Object> deserializedArguments(EntityManager manager) {
		List<Object> result = new ArrayList<>();
		// deserialize the arguments
		for (int i = 0; i < getMethod().parameters.size(); i++) {
			ActionParameterEntry parameter = getMethod().parameters.get(i);
			String arg = arguments.get(i);
			result.add(parameter.parse(arg, manager));
		}
		return result;
	}

	public void serializeAndAddArguments(List<Object> args) {
		// add the arguments
		for (int i = 0; i < getMethod().parameters.size(); i++) {
			ActionParameterEntry entry = getMethod().parameters.get(i);
			arguments.add(entry.generate(args.get(i)));
		}
	}

	public Collection<Right> getRequiredRights() {
		return method.controllerEntry.requiredRights;
	}

	public boolean isEqual(ActionInvocation other) {
		return method == other.method && arguments.equals(other.arguments);
	}*/
}
