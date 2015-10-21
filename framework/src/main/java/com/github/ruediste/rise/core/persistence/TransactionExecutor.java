package com.github.ruediste.rise.core.persistence;

import com.github.ruediste.rise.nonReloadable.persistence.IsolationLevel;

public interface TransactionExecutor {

    <T> T execute(TransactionCallback<T> action);

    void execute(TransactionCallbackNoResult action);

    TransactionExecutor isolation(IsolationLevel level);

    /**
     * Exception to rollback for (if exception is same or subclass of). Can be
     * overridden using {@link #noRollbackFor(Class...)}. If no exceptions are
     * specified, a rollback occurs for all exceptions
     */
    @SuppressWarnings("unchecked")
    TransactionExecutor rollbackFor(Class<? extends Throwable>... exceptions);

    /**
     * Exceptions not to rollback for (if exception is same or subclass of).
     * Takes precedence over {@link #rollbackFor(Class...)}
     */
    @SuppressWarnings("unchecked")
    TransactionExecutor noRollbackFor(Class<? extends Throwable>... exceptions);

    TransactionExecutor updating(boolean value);

    /**
     * Create an updating transaction which can be commited. The default
     * isolation level is set to {@link IsolationLevel#SERIALIZABLE}, but can be
     * overrridden using {@link #isolation(IsolationLevel)}
     */
    TransactionExecutor updating();

    /**
     * If called, a new entity manager set will be used in any case (even with
     * {@link Propagation#NEVER}.
     * <p>
     * By default, a new entity manager set will only be used if a transaction
     * is active and no entity manager set is present.
     */
    TransactionExecutor forceNewEntityManagerSet();

    TransactionExecutor timeout(int seconds);

    TransactionExecutor propagation(Propagation propagation);

}