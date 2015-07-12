package com.github.ruediste.rise.nonReloadable.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Interface for controlling transaction
 */
@Singleton
public class TransactionControl {

    @Inject
    TransactionProperties transactionProperties;

    /**
     * Setup the transaction to force a rollback when the transaction ends
     */
    public void forceRollback() {
        transactionProperties.forceRollback();
    }
}
