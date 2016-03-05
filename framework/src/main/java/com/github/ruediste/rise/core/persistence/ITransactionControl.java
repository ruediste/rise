package com.github.ruediste.rise.core.persistence;

import com.github.ruediste.rise.nonReloadable.persistence.IsolationLevel;

/**
 * Interface for programmatic transaction control.
 *
 */
public interface ITransactionControl {

	/**
	 * Execute an action in the transaction according to the current
	 * configuration of the control
	 */
	<T> T execute(TransactionCallback<T> action);

	/**
	 * Execute an action in the transaction according to the current
	 * configuration of the control
	 */
	void execute(TransactionCallbackNoResult action);

	/**
	 * Set the isolation level to be used if a new transaction needs to be
	 * started. By default {@link IsolationLevel#DEFAULT} is used.
	 */
	ITransactionControl isolation(IsolationLevel level);

	/**
	 * Exception to rollback for (if exception is same or subclass of). Can be
	 * overridden using {@link #noRollbackFor(Class...)}. If no exceptions are
	 * specified, a rollback occurs for all exceptions
	 */
	@SuppressWarnings("unchecked")
	ITransactionControl rollbackFor(Class<? extends Throwable>... exceptions);

	/**
	 * Exceptions not to rollback for (if exception is same or subclass of).
	 * Takes precedence over {@link #rollbackFor(Class...)}
	 */
	@SuppressWarnings("unchecked")
	ITransactionControl noRollbackFor(Class<? extends Throwable>... exceptions);

	/**
	 * Set if the transactino is updating or not.
	 * 
	 * @see #updating()
	 */
	ITransactionControl updating(boolean value);

	/**
	 * Create an updating transaction which can be commited. The default
	 * isolation level is set to {@link IsolationLevel#SERIALIZABLE}, but can be
	 * overrridden using {@link #isolation(IsolationLevel)}
	 */
	ITransactionControl updating();

	/**
	 * If called, a new entity manager set will be used in any case (even with
	 * {@link Propagation#NEVER}.
	 * <p>
	 * By default, a new entity manager set will only be used if a transaction
	 * is active and no entity manager set is present.
	 */
	ITransactionControl forceNewEntityManagerSet();

	/**
	 * Set the transaction timeout in seconds
	 */
	ITransactionControl timeout(int seconds);

	/**
	 * Set the propagation
	 */
	ITransactionControl propagation(Propagation propagation);

}