package laf.component.core.impl;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import laf.base.ActionResult;
import laf.component.core.ControllerUtil;
import laf.component.pageScope.PageScoped;
import laf.persistence.LafPersistenceContextManager;
import laf.persistence.LafPersistenceHolder;

public class ControllerUtilImpl implements ControllerUtil {

	private ActionResult errorDestination;
	private ActionResult destination;

	@Inject
	UserTransaction trx;

	@Inject
	LafPersistenceContextManager manager;

	@Inject
	Instance<LafPersistenceHolder> holderInstance;

	@Inject
	@PageScoped
	Instance<LafPersistenceHolder> pageScopedHolderInstance;

	@Override
	public void commit() {
		checkAndCommit(null, null);
	}

	@Override
	public void commit(Runnable inTransaction) {
		checkAndCommit(null, inTransaction);
	}

	@Override
	public void checkAndCommit(Runnable checker) {
		checkAndCommit(checker, null);
	}

	@Override
	public void checkAndCommit(Runnable checker, Runnable inTransaction) {
		try {
			trx.begin();
			if (checker != null) {
				LafPersistenceHolder holder = holderInstance.get();
				manager.withPersistenceHolder(holder, checker);
				holder.destroy();
			}
			LafPersistenceHolder pageScopeHolder = pageScopedHolderInstance
					.get();
			pageScopeHolder.joinTransaction();
			if (inTransaction != null) {
				manager.withPersistenceHolder(pageScopeHolder, inTransaction);

			}
			trx.commit();
		} catch (NotSupportedException | SystemException
				| IllegalStateException | SecurityException
				| HeuristicMixedException | HeuristicRollbackException
				| RollbackException e) {
			throw new RuntimeException(e);
		}
	}

	public ActionResult getErrorDestination() {
		return errorDestination;
	}

	@Override
	public void setErrorDestination(ActionResult target) {
		errorDestination = target;

	}

	public ActionResult getDestination() {
		return destination;
	}

	@Override
	public void setDestination(ActionResult target) {
		destination = target;
	}

}
