package com.github.ruediste.rise.util;

import java.util.Objects;

public class Tripple<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    public Tripple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Tripple<?, ?, ?> other = (Tripple<?, ?, ?>) obj;
        return Objects.equals(a, other.a) && Objects.equals(b, other.b) && Objects.equals(c, other.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }

    public static <A, B, C> Tripple<A, B, C> create(A a, B b, C c) {
        return new Tripple<A, B, C>(a, b, c);
    }

    public static <A, B, C> Tripple<A, B, C> of(A a, B b, C c) {
        return new Tripple<A, B, C>(a, b, c);
    }

    @Override
    public String toString() {
        return "(" + a + "," + b + "," + c + ")";
    }
}
