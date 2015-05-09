package com.github.ruediste.rise.util;


/**
 * A {@link Runnable} which can be chained to other runnables.
 */
public abstract class ChainedRunnable implements Runnable {

	private Runnable next;

	public abstract void run(Runnable next);

	@Override
	public void run() {
		run(getNext());
	}

	public Runnable getNext() {
		return next;
	}

	public void setNext(Runnable next) {
		this.next = next;
	}

}
