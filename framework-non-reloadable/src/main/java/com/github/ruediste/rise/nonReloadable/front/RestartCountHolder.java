package com.github.ruediste.rise.nonReloadable.front;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.NonRestartable;

@NonRestartable
@Singleton
public class RestartCountHolder {

	@Inject
	CoreConfigurationNonRestartable config;

	long restartNr = 0;
	private Object lock = new Object();

	public void increment() {
		synchronized (lock) {
			restartNr++;
			lock.notifyAll();
		}
	}

	public long get() {
		return restartNr;
	}

	/**
	 * Wait for a restart, if the current {@link #restartNr} matches the
	 * expected Nr. Otherwise returns immediately
	 * 
	 * @return true if a restart occured
	 */
	public boolean waitForRestart(long expectedNr) {
		synchronized (lock) {
			if (expectedNr != restartNr)
				return true;
			try {
				lock.wait(config.restartQueryTimeout);
			} catch (InterruptedException e) {
				// swallow
			}
			if (expectedNr != restartNr)
				return true;
			return false;
		}
	}
}
