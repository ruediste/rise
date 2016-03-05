package com.github.ruediste.rise.core.persistence;

import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;

/**
 * Callback for transactional code. Typically used as argument to
 * {@link TransactionControl#execute(TransactionCallback)}
 */
public interface TransactionCallback<T> {

    T doInTransaction();

    /**
     * Called before the {@link EntityManagerSet} is created. Can be used to
     * initialize transaction properties
     */
    default void beforeEntityManagerSetCreated() {
    }
}
