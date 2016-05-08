package com.github.ruediste.rise.component.fragment;

import java.util.function.Supplier;

/**
 * Represents a value in the view.
 * 
 * <p>
 * In Rise, there is a controller and a view state. Whenever the view is
 * reloaded, the view state is updated based on the values of the
 * {@link HtmlFragment}s, the request is processed and finally the view is
 * updated again from the view state.
 * 
 * <p>
 * When the controller decides to trigger the binding, all bindings are applied
 * from the view state to the controller state.
 * 
 * <img src="doc-files/viewAndControllerState.png" alt=
 * "Overview of Ui State, View State and Controller State">
 * 
 * <p>
 * The view state can be represented by variables in the view,
 * 
 */
public interface IViewValue<T> extends Supplier<T> {

    void set(T value);
}
