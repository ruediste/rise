package com.github.ruediste.rise.util;

/**
 * A {@link Runnable} which can be chained to other runnables.
 */
public abstract class ChainedRunnable implements Runnable {

    private Runnable next;

    /**
     * implementation of this runnable. Call {@code next.run()} to invoke the
     * remaining runnables of this chain
     */
    public abstract void run(Runnable next);

    @Override
    final public void run() {
        run(getNext());
    }

    final public Runnable getNext() {
        return next;
    }

    final public void setNext(Runnable next) {
        this.next = next;
    }

}
