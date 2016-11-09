package com.github.ruediste.rise.util;

/**
 * A Mutable variable
 */
public class Var<T> {

    private T value;

    public Var() {
    }

    public Var(T value) {
        super();
        this.value = value;
    }

    public static <T> Var<T> of(T value) {
        return new Var<>(value);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
