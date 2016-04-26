package com.github.ruediste.rise.component.fragment;

public class ViewValue<T> implements IViewValue<T> {

    private T value;

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

}
