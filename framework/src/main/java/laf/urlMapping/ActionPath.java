package laf.urlMapping;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.base.Objects;

/**
 * An action path represents an invocation of an action method. It is composed
 * of {@link ActionInvocation} elements, which describe the path over possibly
 * multiple embedded controllers to the final action.
 */

public class ActionPath<T> {
	// public FlashMessage flashMessage;
	// public HttpMethod forcedHttpMethod;

	private final ArrayList<ActionInvocation<T>> elements = new ArrayList<>();
	public Integer statefulControllerId;

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

	/**
	 * Convert an {@link ActionPath} with {@link ParameterValueProvider}s to an
	 * ActionPath with {@link Object}s, using the
	 * {@link ParameterValueProvider#provideValue()}
	 */
	public static ActionPath<Object> createObjectActionPath(
			ActionPath<ParameterValueProvider> actionPath) {
		ActionPath<Object> result = new ActionPath<Object>();
		for (ActionInvocation<ParameterValueProvider> invocation : actionPath
				.getElements()) {
			ActionInvocation<Object> i = new ActionInvocation<Object>(
					invocation);
			for (ParameterValueProvider provider : invocation.getArguments()) {
				i.getArguments().add(provider.provideValue());
			}
			result.getElements().add(i);
		}
		return result;
	}
}