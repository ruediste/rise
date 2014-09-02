package laf.mvc.core;

import java.lang.reflect.Type;
import java.util.*;

import javax.enterprise.inject.Alternative;

import laf.component.core.ActionInvocation;
import laf.core.base.Function2;
import laf.core.base.MethodInvocation;
import laf.core.base.attachedProperties.AttachedProperty;
import laf.core.base.attachedProperties.AttachedPropertyBearerBase;

import com.google.common.base.Function;
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
@Alternative
public class ActionPath<T> extends AttachedPropertyBearerBase {

	final HashMap<ActionPathParameter, String> parameters = new HashMap<>();

	private final ArrayList<MethodInvocation<T>> elements = new ArrayList<>();

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(getElements()).toString();
	}

	/*
	 * public Set<Right> getRequiredRights() { Set<Right> result = new
	 * HashSet<>(); for (ActionInvocation element : elements) {
	 * result.addAll(element.getRequiredRights()); } return result; }
	 */

	public ArrayList<MethodInvocation<T>> getElements() {
		return elements;
	}

	/**
	 * Determine if this and the other path represent calls to the same action
	 * method. The parameters values are ignored.
	 */
	public boolean isCallToSameActionMethod(ActionPath<?> other) {
		return isCallToSameActionMethod(other,
				new MethodInvocation.ParameterValueComparator<Object, Object>() {

					@Override
					public boolean equals(Object a, Object b) {
						return true;
					}
				});
	}

	/**
	 * Determine if this and the other path represent calls to the same action
	 * method. The Parameters are compared by the provided
	 */
	public <O> boolean isCallToSameActionMethod(ActionPath<O> other,
			MethodInvocation.ParameterValueComparator<? super T, ? super O> comparator) {
		if (elements.size() != other.getElements().size()) {
			return false;
		}

		Iterator<MethodInvocation<T>> it = elements.iterator();
		Iterator<MethodInvocation<O>> oit = other.getElements().iterator();

		while (it.hasNext() && oit.hasNext()) {
			if (!it.next().isCallToSameMethod(oit.next(), comparator)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Create a new {@link ActionPath} with the arguments mapped by the given
	 * function. The associated {@link ActionPathParameter}s are copied
	 */
	public <P> ActionPath<P> map(final Function<? super T, P> func) {
		return mapWithType(new Function2<Type, T, P>() {

			@Override
			public P apply(Type a, T b) {
				return func.apply(b);
			}
		});
	}

	public <P> ActionPath<P> mapWithType(Function2<Type, ? super T, P> func) {
		ActionPath<P> result = new ActionPath<>();
		result.parameters.putAll(parameters);

		for (MethodInvocation<T> element : elements) {
			MethodInvocation<P> invocation = element.map(func);
			result.getElements().add(invocation);
		}
		return result;
	}

	public MethodInvocation<T> getFirst() {
		return elements.get(0);
	}

	public MethodInvocation<T> getLast() {
		return elements.get(elements.size() - 1);
	}
}
