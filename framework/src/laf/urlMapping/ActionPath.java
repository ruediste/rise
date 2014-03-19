package laf.urlMapping;

import java.util.*;

import com.google.common.base.Objects;

/**
 * An action path represents an invocation of an action. It is composed of
 * {@link ActionInvocation} elements, which describe the path over possibly
 * multiple embedded controllers to the final action.
 */

public class ActionPath<T> implements Iterable<ActionInvocation> {
	// public FlashMessage flashMessage;
	// public HttpMethod forcedHttpMethod;

	private ArrayList<ActionInvocation> elements = new ArrayList<>();
	public Integer statefulControllerId;

	@Override
	public Iterator<ActionInvocation> iterator() {
		return elements.iterator();
	}

	public void add(ActionInvocation element) {
		elements.add(element);
	}

	public int size() {
		return elements.size();
	}

	public void addAll(Collection<? extends ActionInvocation> newElements) {
		elements.addAll(newElements);
	}

	public void prepend(Collection<? extends ActionInvocation> elements) {
		this.elements.addAll(0, elements);
	}

	public ActionInvocation statefulControllerInvocation() {
		for (ActionInvocation element : elements) {
			// if (element.getMethod().controllerEntry.isStatefulController) {
			// return element;
			// }
		}
		return null;
	}

	public ActionInvocation first() {
		return elements.get(0);
	}

	public ActionInvocation last() {
		return elements.get(elements.size() - 1);
	}

	public boolean isSerializable() {
		// return first().getMethod().isSerializable;
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(elements).toString();
	}

	/*public Set<Right> getRequiredRights() {
		Set<Right> result = new HashSet<>();
		for (ActionInvocation element : elements) {
			result.addAll(element.getRequiredRights());
		}
		return result;
	}*/

	/**
	 * test if this path starts with the given {@link ActionInvocation}s
	 */
	public boolean isCallToStatefulController(
			Iterable<ActionInvocation> invocationsOfInstatiation,
			Class<?> controllerClass) {
		Iterator<ActionInvocation> it = invocationsOfInstatiation.iterator();
		Iterator<ActionInvocation> eit = elements.iterator();

		// iterate an check if the elements are equal
		while (it.hasNext() && eit.hasNext()) {
			// if (!it.next().isEqual(eit.next())) {
			// return false;
			// }
		}

		if (it.hasNext()) {
			// there were unconsumed entries in the invocations, thus
			// this path was shorter than invocations
			return false;
		}

		if (!eit.hasNext()) {
			// there are no more elements, thus the call to a method in the
			// stateful controller is missing
			return false;
		}

		// check the controller class
		// return eit.next().getMethod().controllerEntry.clazz ==
		// controllerClass;
		return false;
	}
}
