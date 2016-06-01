package com.github.ruediste.rise.component.fragment;

public class ValueHandleImpl<T> implements ValueHandle<T> {

    private T value;

    public ValueHandleImpl() {
    }

    public ValueHandleImpl(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }
}
