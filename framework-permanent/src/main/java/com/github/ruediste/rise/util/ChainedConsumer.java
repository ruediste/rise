package com.github.ruediste.rise.util;

import java.util.function.Consumer;

/**
 * A {@link Consumer} which can be chained to other consumers.
 */
public abstract class ChainedConsumer<T> implements Consumer<T> {

	private Consumer<? super T> next;

	public abstract void accept(T t, Consumer<? super T> next);

	@Override
	public void accept(T t) {
		accept(t, getNext());
	}

	public Consumer<? super T> getNext() {
		return next;
	}

	public void setNext(Consumer<? super T> next) {
		this.next = next;
	}

}
