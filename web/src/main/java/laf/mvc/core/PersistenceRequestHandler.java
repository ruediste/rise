package laf.mvc.core;

import javax.inject.Inject;
import javax.transaction.*;

import laf.core.base.ActionResult;
import laf.core.persistence.LafPersistenceContextManager;

/**
 * Controller managing transactions and entity managers
 */
public class PersistenceRequestHandler extends
		DelegatingRequestHandler<String, String> {

	@Inject
	UserTransaction transaction;

	@Inject
	LafPersistenceContextManager contextManager;

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
					transaction.setRollbackOnly();
				}

				return getDelegate().handle(actionPath);
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
