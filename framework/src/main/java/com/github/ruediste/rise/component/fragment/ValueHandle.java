package com.github.ruediste.rise.component.fragment;

import java.util.function.Supplier;

public interface ValueHandle<T> extends Supplier<T> {

    @Override
    T get();

    void set(T value);
}
