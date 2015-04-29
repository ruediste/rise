package com.github.ruediste.laf.core.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.TransactionSynchronizationRegistry;

import com.github.ruediste.laf.util.Pair;

@Singleton
public class TransactionProperties {

	@Inject
	TransactionSynchronizationRegistry registry;

	private Object isolationLevel = new Object();

	/**
	 * Set the isolation level of the current transaction for the given data
	 * source
	 */
	public void setIsolationLevel(Class<?> qualifier, IsolationLevel level) {
		registry.putResource(Pair.of(isolationLevel, qualifier), level);
	}

	/**
	 * Get the isolation level of the current transaction
	 * 
	 * @return
	 */
	public IsolationLevel getIsolationLevel(Class<?> qualifier) {
		return (IsolationLevel) registry.getResource(Pair.of(isolationLevel,
				qualifier));
	}
}
