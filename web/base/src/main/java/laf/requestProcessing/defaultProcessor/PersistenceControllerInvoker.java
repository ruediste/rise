package laf.requestProcessing.defaultProcessor;

import javax.inject.Inject;
import javax.transaction.*;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.requestProcessing.ControllerInvoker;

/**
 * Controller managing transactions and entity managers
 */
public class PersistenceControllerInvoker implements ControllerInvoker {

	private ControllerInvoker delegate;

	@Inject
	UserTransaction transaction;

	// @Inject
	// EntityManagerFactory factory;

	@Override
	public ActionResult invoke(ActionPath<Object> actionPath) {
		try {
			try {
				transaction.begin();
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
