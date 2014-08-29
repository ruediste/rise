package laf.component.core;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.*;

import laf.component.core.reqestProcessing.PageScopedPersistenceHolder;
import laf.core.base.ActionResult;
import laf.core.persistence.LafPersistenceContextManager;
import laf.core.persistence.LafPersistenceHolder;

import org.slf4j.Logger;

public class ControllerUtilBase {

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

	public void commit() {
		checkAndCommit(null, null);
	}

	public void commit(Runnable inTransaction) {
		checkAndCommit(null, inTransaction);
	}

	public void checkAndCommit(Runnable checker) {
		checkAndCommit(checker, null);
	}

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
			log.debug("commiting holder " + pageScopeHolder.toString());
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

	public void setErrorDestination(ActionResult target) {
		errorDestination = target;

	}

	public ActionResult getDestination() {
		return destination;
	}

	public void setDestination(ActionResult target) {
		destination = target;
	}

}
