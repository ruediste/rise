package com.github.ruediste.laf.component.core;

import javax.inject.Inject;
import javax.transaction.*;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.core.base.Val;
import com.github.ruediste.laf.core.persistence.PersistenceUnitTokenManager;

public class PersistenceInitialRequestHandler
		extends
		DelegatingRequestHandler<ActionInvocation<String>, ActionInvocation<String>> {

	@Inject
	Logger log;

	@Inject
	PersistenceUnitTokenManager manager;

	@Inject
	PageScopedPersistenceHolder holder;

	@Inject
	UserTransaction trx;

	@Override
	public ActionResult handle(final ActionInvocation<String> path) {
		final Val<ActionResult> result = new Val<>();
		try {
			trx.begin();
			manager.withPersistenceHolder(holder, new Runnable() {

				@Override
				public void run() {
					result.set(getDelegate().handle(path));
				}
			});
			trx.commit();
			return result.get();
		} catch (NotSupportedException | SystemException
				| IllegalStateException | SecurityException
				| HeuristicMixedException | HeuristicRollbackException
				| RollbackException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (trx.getStatus() == Status.STATUS_ACTIVE
						|| trx.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
					trx.rollback();
				}
			} catch (IllegalStateException | SecurityException
					| SystemException e) {
				log.error("error while rolling back transaction", e);
			}
		}
	}

}
