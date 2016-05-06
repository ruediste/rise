package com.github.ruediste.rise.component.fragment;

import java.util.function.Supplier;

/**
 * Represents a value in the view.
 * 
 */
public interface IViewValue<T> extends Supplier<T> {

    void set(T value);
}
