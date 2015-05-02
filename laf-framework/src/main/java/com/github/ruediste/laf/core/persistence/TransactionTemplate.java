package com.github.ruediste.laf.core.persistence;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.laf.core.persistence.em.EntityManagerSet;
import com.github.ruediste.laf.mvc.web.MvcWebRequestInfo;
import com.github.ruediste.laf.mvc.web.TransactionException;

public class TransactionTemplate {

	@Inject
	Logger log;

	@Inject
	TransactionManager txm;

	@Inject
	EntityManagerHolder holder;

	@Inject
	MvcWebRequestInfo info;

	@Inject
	TransactionProperties transactionProperties;

	public class TransactionBuilder {
		private boolean useNewEntityManagerSet = true;
		private boolean updating = false;

		public TransactionBuilder reuseEntityManagerSet() {
			useNewEntityManagerSet = false;
			return this;
		}

		public TransactionBuilder updating() {
			updating = true;
			return this;
		}

		public TransactionBuilder updating(boolean value) {
			updating = value;
			return this;
		}

		public void execute(TransactionCallbackNoResult action) {
			execute(new TransactionCallback<Object>() {

				@Override
				public void beforeEntityManagerSetCreated() {
					action.beforeEntityManagerSetCreated();
				}

				@Override
				public Object doInTransaction(TransactionControl trx) {
					action.doInTransaction(trx);
					return null;
				}
			});
		}

		public <T> T execute(TransactionCallback<T> action) {
			boolean entityManagerSetWasSet = false;
			try {
				txm.begin();

				transactionProperties
						.setDefaultIsolationLevel(updating ? IsolationLevel.SERIALIZABLE
								: IsolationLevel.REPEATABLE_READ);

				if (useNewEntityManagerSet) {
					action.beforeEntityManagerSetCreated();

					holder.setNewEntityManagerSet();
					entityManagerSetWasSet = true;
				}

				T result = action.doInTransaction(new TransactionControl() {

					@Override
					public void commit() {
						try {
							txm.commit();
						} catch (SecurityException | IllegalStateException
								| RollbackException | HeuristicMixedException
								| HeuristicRollbackException | SystemException e) {
							throw new TransactionException(
									"Error during commit", e);
						}
					}
				});

				return result;
			} catch (NotSupportedException | SystemException e) {
				throw new TransactionException("Transaction error occured", e);
			} finally {
				if (useNewEntityManagerSet && entityManagerSetWasSet) {
					holder.closeCurrentEntityManagers();
					holder.removeCurrentSet();
				}

				Integer status = null;
				try {
					status = txm.getStatus();
					if (status != Status.STATUS_NO_TRANSACTION)
						txm.rollback();
				} catch (IllegalStateException | SecurityException
						| SystemException e) {
					log.error("Error during transaction rollback. Status was "
							+ status, e);
				}

			}
		}
	}

	/**
	 * Create a new {@link TransactionBuilder} with default settings (
	 * non-updating, using a fresh {@link EntityManagerSet})
	 */
	public TransactionBuilder builder() {
		return new TransactionBuilder();
	}

}
