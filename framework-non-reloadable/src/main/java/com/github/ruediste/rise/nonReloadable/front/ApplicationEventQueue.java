package com.github.ruediste.rise.nonReloadable.front;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Singleton;

import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Event queue to process application events, such as {@link RestartableApplication}
 * restarts etc. All events will be processed in the Applicatio Event Thread
 * (AET)
 */
@Singleton
@NonRestartable
public class ApplicationEventQueue extends ScheduledThreadPoolExecutor {

	private Thread queueThread;

	public ApplicationEventQueue() {
		super(1, new ThreadFactoryBuilder().setDaemon(true)
				.setNameFormat("AET").build());
		setRemoveOnCancelPolicy(true);
		setThreadFactory(getThreadFactory());
		try {
			queueThread = submit(() -> Thread.currentThread()).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Determine if the current thread is the AET.
	 */
	public boolean isOnAET() {
		return Thread.currentThread().equals(queueThread);
	}

	/**
	 * Raise an exception if the current thread is not the AET
	 */
	public void checkAET() {
		if (!isOnAET()) {
			throw new RuntimeException("Not on Application Event Thread (AET)");
		}
	}
}
