package com.github.ruediste.laf.core.base;

public interface ThrowingConsumer2<A, B> {

	void accept(A a, B b) throws Throwable;
}