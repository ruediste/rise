package laf.urlMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * An invocation of an action method in the serialized form. All arguments are
 * represented by strings.
 */
public class ActionInvocation {
	public final List<String> arguments = new ArrayList<String>();

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
