package com.github.ruediste.laf.core.base;

import java.util.Objects;

public class Pair<A, B> {
	private final A a;
	private final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;

	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(a, other.a) && Objects.equals(b, other.b);
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b);
	}

	public static <A, B> Pair<A, B> create(A a, B b) {
		return new Pair<A, B>(a, b);
	}

	public static <A, B> Pair<A, B> of(A a, B b) {
		return new Pair<A, B>(a, b);
	}

	@Override
	public String toString() {
		return "(" + a + "," + b + ")";
	}
}
