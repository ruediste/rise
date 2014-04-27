package laf.actionPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import laf.attachedProperties.AttachedProperty;
import laf.attachedProperties.AttachedPropertyBearerBase;

import com.google.common.base.Objects;

/**
 * An action path represents an invocation of an action method. It is composed
 * of {@link ActionInvocation} elements, which describe the path over possibly
 * multiple embedded controllers to the final action.
 *
 * <p>
 * {@link AttachedProperty AttachedProperties} can be attached to an action
 * path, allowing other modules to transport information via action paths.
 * However, they are not sent along with an action path to the client
 * </p>
 *
 * <p>
 * Using {@link ActionPathParameter}, arbitrary strings can be attached to
 * {@link ActionPath}s.
 * </p>
 *
 */
public class ActionPath<T> extends AttachedPropertyBearerBase {
	// public FlashMessage flashMessage;
	// public HttpMethod forcedHttpMethod;
	// public Integer statefulControllerId;

	final HashMap<ActionPathParameter, String> parameters = new HashMap<>();

	private final ArrayList<ActionInvocation<T>> elements = new ArrayList<>();

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(getElements()).toString();
	}

	/*
	 * public Set<Right> getRequiredRights() { Set<Right> result = new
	 * HashSet<>(); for (ActionInvocation element : elements) {
	 * result.addAll(element.getRequiredRights()); } return result; }
	 */

	public ArrayList<ActionInvocation<T>> getElements() {
		return elements;
	}

	/**
	 * Determine if this and the other path represent calls to the same action
	 * method. The parameters values are ignored.
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

	/**
	 * When comparing two {@link ActionPath}s with different argument
	 * representations using
	 * {@link ActionPath#isCallToSameActionMethod(ActionPath, ParameterValueComparator)}
	 * , providing an implementation of this interface allows to specify a
	 * strategy to compare the arguments.
	 */
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
