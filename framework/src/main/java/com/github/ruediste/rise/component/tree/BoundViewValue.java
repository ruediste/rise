package com.github.ruediste.rise.component.tree;

public class BoundViewValue<T> implements IViewValue<T> {

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
