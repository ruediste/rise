package com.github.ruediste.rise.core.persistence;

/**
 * Callback for transactional code. Typically used as argument to
 * {@link TransactionControl#execute(TransactionCallbackNoResult)}
 */
public interface TransactionCallbackNoResult {

    void doInTransaction();

}
