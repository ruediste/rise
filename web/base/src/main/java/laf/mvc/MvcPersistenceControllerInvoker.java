package laf.mvc;

import javax.inject.Inject;
import javax.transaction.*;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.persistence.LafPersistenceContextManager;
import laf.requestProcessing.ControllerInvoker;

/**
 * Controller managing transactions and entity managers
 */
public class MvcPersistenceControllerInvoker implements ControllerInvoker {

	private ControllerInvoker delegate;

	@Inject
	UserTransaction transaction;

	@Inject
	LafPersistenceContextManager contextManager;

	@Override
	public ActionResult invoke(ActionPath<Object> actionPath) {
		boolean updating = actionPath.getLast().getMethodInfo().isUpdating();
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

				return delegate.invoke(actionPath);
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

	public ControllerInvoker getDelegate() {
		return delegate;
	}

	public void setDelegate(ControllerInvoker delegate) {
		this.delegate = delegate;
	}

}
