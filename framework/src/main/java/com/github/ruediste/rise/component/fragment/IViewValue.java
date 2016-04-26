package com.github.ruediste.rise.component.fragment;

import java.util.function.Supplier;

public interface IViewValue<T> extends Supplier<T> {

    void set(T value);
}
