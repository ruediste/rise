package laf.urlMapping;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.common.base.Objects;

/**
 * An action path represents an invocation of an action method. It is composed
 * of {@link ActionInvocation} elements, which describe the path over possibly
 * multiple embedded controllers to the final action.
 */

public class ActionPath<T> {
	// public FlashMessage flashMessage;
	// public HttpMethod forcedHttpMethod;

	private final LinkedList<ActionInvocation<T>> elements = new LinkedList<>();
	public Integer statefulControllerId;

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(getElements()).toString();
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
		Iterator<ActionInvocation<T>> eit = getElements().iterator();

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

	public LinkedList<ActionInvocation<T>> getElements() {
		return elements;
	}

	/**
	 * Determine if this and the other path represent calls to the same action
	 * method. The Parameters are not compared.
	 */
	public boolean isCallToSameActionMethod(ActionPath<?> other) {
		return isCallToSameActionMethod(other,
				new ParameterValueComparator<Object, Object>() {

					@Override
					public boolean equals(Object a, Object b) {
						return true;
					}
				});
	}

	public interface ParameterValueComparator<A, B> {
		public boolean equals(A a, B b);
	}

	/**
	 * Determine if this and the other path represent calls to the same action
	 * method. The Parameters are compared by the provided
	 */
	public <O> boolean isCallToSameActionMethod(ActionPath<O> other,
			ParameterValueComparator<? super T, ? super O> comparator) {
		if (elements.size() != other.getElements().size()) {
			return false;
		}

		Iterator<ActionInvocation<T>> it = elements.iterator();
		Iterator<ActionInvocation<O>> oit = other.getElements().iterator();

		while (it.hasNext() && oit.hasNext()) {
			if (!it.next().isCallToSameActionMethod(oit.next(), comparator)) {
				return false;
			}
		}
		return true;
	}
}
