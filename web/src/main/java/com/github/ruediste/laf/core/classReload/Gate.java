package com.github.ruediste.laf.core.classReload;

/**
 * A gate which starts closed and can be opened at some point
 */
public class Gate {

	private boolean isOpen;
	private final Object lock = new Object();

	public Gate() {
		this(false);
	}

	public Gate(boolean isOpen) {
		this.isOpen = isOpen;
	}

	/**
	 * Pass the gate. If the gate is closed, wait until it is opened
	 */
	public void pass() {
		synchronized (lock) {
			if (!isOpen)
				try {
					lock.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
		}
	}

	/**
	 * Open the gate
	 */
	public void open() {
		synchronized (lock) {
			isOpen = true;
			lock.notifyAll();
		}
	}

	public void close() {
		synchronized (lock) {
			isOpen = false;
		}
	}
}
