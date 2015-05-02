package com.github.ruediste.laf.core.persistence;

import com.github.ruediste.laf.core.persistence.em.EntityManagerSet;

/**
 * Callback for transactional code. Typically used as argument to {@link
 * TransactionTemplate#do}
 */
public interface TransactionCallbackNoResult {

	void doInTransaction(TransactionControl trx);

	/**
	 * Called before the {@link EntityManagerSet} is created. Can be used to
	 * initialize transaction properties
	 */
	default void beforeEntityManagerSetCreated() {
	}
}
