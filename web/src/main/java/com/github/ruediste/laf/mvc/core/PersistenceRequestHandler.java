package com.github.ruediste.laf.mvc.core;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.*;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.core.base.Val;
import com.github.ruediste.laf.core.persistence.LafPersistenceHolder;
import com.github.ruediste.laf.core.persistence.PersistenceUnitTokenManager;

/**
 * Controller managing transactions and entity managers
 */
public class PersistenceRequestHandler extends
		DelegatingRequestHandler<String, String> {

	@Inject
	UserTransaction transaction;

	@Inject
	PersistenceUnitTokenManager contextManager;

	@Inject
	Instance<LafPersistenceHolder> holderInstance;

	@Override
	public ActionResult handle(ActionPath<String> actionPath) {
		boolean updating = ControllerReflectionUtil.isUpdating(actionPath
				.getLast().getMethod());
		if (updating) {
			// TODO: start serializable transaction
		}
		try {
			try {
				transaction.begin();

				// only allow rollback for non-updating actions
				if (!updating) {
					// transaction.setRollbackOnly();
				}

				LafPersistenceHolder holder = holderInstance.get();
				Val<ActionResult> result = new Val<>();
				contextManager.withPersistenceHolder(holder, () -> {
					result.set(getDelegate().handle(actionPath));
				});
				holder.destroy();
				return result.get();
			} finally {
				if (transaction.getStatus() == Status.STATUS_ACTIVE
						|| transaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
					transaction.rollback();
				}
			}
		} catch (SystemException | NotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
