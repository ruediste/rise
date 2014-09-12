package laf.component.core.binding;

import java.io.Serializable;

import laf.component.core.tree.ComponentBase;

/**
 * A group of bindings managed by a controller.
 *
 * <p>
 * Data binding is used to move data between the domain objects and the view
 * components. Properties are bound together in a typesafe manner using lambda
 * expressions.
 * </p>
 *
 * <p>
 * The binding is triggered explicitly. The controller instantiates a
 * {@link BindingGroup} and references it in an instance variable. If the data
 * is ready, the controller uses the {@link #pullUp(Object)} method to fill the
 * components with data. To retrieve the data from the components, it uses
 * {@link #pushDown(Object)}. To the view the binding group is exposed via a
 * function returning {@link #proxy()}.
 * </p>
 *
 * <p>
 * The component classes have a
 * {@link ComponentBase#bind(java.util.function.Consumer)} method which accepts
 * a lambda expression. The lambda expression has the component as parameter and
 * sets a property of the component to some value retrieved via a binding group
 * exposed by the controller.
 * </p>
 *
 * <p>
 * While establishing the binding, the lambda expression is called with a
 * dynamic proxy of the component class as parameter. During the invocation,
 * {@link BindingGroup#proxy()} is configured to return a dynamic proxy, too.
 * The two dynamic proxies record the methods invoked on themselves. This
 * information is used to determine which properties are accessed.
 * </p>
 *
 * <p>
 * Bindings can be
 * <dl>
 * <dt> {@link BindingDirection#TWO_WAY TWO_WAY}</dt>
 * <dd>Bididrectional binding between view and model property</dd>
 * <dt> {@link BindingDirection#ONE_WAY ONE_WAY}</dt>
 * <dd>Reading a property of the model and setting a property of the view</dd>
 * <dt> {@link BindingDirection#ONE_WAY_TO_MODEL ONE_WAY_TO_MODEL}</dt>
 * <dd>Reading a property of the view and setting a property of the model</dd>
 * </dl>
 * For TWO_WAY bindings both involved properties have to be readable and
 * writeable.
 * </p>
 * <p>
 * Both the value returned from {@link BindingGroup#proxy()} and the component
 * instance passed to the binding lambda expression are dynamic proxies which
 * record the methods being called. When the lambda expression returns, this
 * information is used to determine the properties which were accessed and to
 * construct a bi-directional between them.
 * </p>
 *
 * <p>
 * In the case of one-way bindings, the lambda expression is used to directly
 * transfer the data.
 * </p>
 */
public class BindingGroup<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public T proxy() {
		return null;
	}

	public void pullUp(T data) {

	}

	public void pushDown(T data) {

	}
}
