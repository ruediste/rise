package laf.component.core.impl;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.*;

import laf.base.ActionResult;
import laf.component.core.ControllerUtil;
import laf.component.reqestProcessing.PageScopedPersistenceHolder;
import laf.persistence.*;

import org.slf4j.Logger;

public class ControllerUtilImpl implements ControllerUtil {

	private ActionResult errorDestination;
	private ActionResult destination;

	@Inject
	Logger log;

	@Inject
	UserTransaction trx;

	@Inject
	LafPersistenceContextManager manager;

	@Inject
	Instance<LafPersistenceHolder> holderInstance;

	@Inject
	Instance<PageScopedPersistenceHolder> pageScopedHolderInstance;

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
			log.debug("commiting holder " + pageScopeHolder.implToString());
			pageScopeHolder.joinTransaction();
			if (inTransaction != null) {
				inTransaction.run();
				// manager.withPersistenceHolder(pageScopeHolder,
				// inTransaction);
			}
			pageScopeHolder.flush();
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
