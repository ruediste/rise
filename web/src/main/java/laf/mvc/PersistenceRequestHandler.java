package laf.mvc;

import javax.inject.Inject;
import javax.transaction.*;

import laf.base.ActionResult;
import laf.core.persistence.LafPersistenceContextManager;
import laf.mvc.actionPath.ActionPath;
import laf.mvc.actionPath.ControllerReflectionUtil;

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