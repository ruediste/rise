package com.github.ruediste.rise.core.front;

import javax.inject.Singleton;

import com.github.ruediste.rise.core.Permanent;

@Permanent
@Singleton
public class ReloadCountHolder {

	long reloadNr = 0;
	private Object lock = new Object();

	public void increment() {
		synchronized (lock) {
			reloadNr++;
			lock.notifyAll();
		}
	}

	public long get() {
		return reloadNr;
	}

	/**
	 * Wait for a reload, if the current reloadNr matches the expected Nr.
	 * Otherwise returns immediately
	 * 
	 * @return true if a reload occured
	 */
	public boolean waitForReload(long expectedNr) {
		synchronized (lock) {
			if (expectedNr != reloadNr)
				return true;
			try {
				lock.wait(5000);
			} catch (InterruptedException e) {
				// swallow
			}
			if (expectedNr != reloadNr)
				return true;
			return false;
		}
	}
}
