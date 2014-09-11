package laf.component.core;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.*;

import laf.core.base.ActionResult;
import laf.core.http.HttpService;
import laf.core.persistence.LafPersistenceContextManager;
import laf.core.persistence.LafPersistenceHolder;

import org.slf4j.Logger;

public class ControllerUtilBase {

	private ActionResult errorDestination;
	private String destinationUrl;

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

	@Inject
	protected HttpService httpService;

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
		boolean commited = false;
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
			}
			pageScopeHolder.flush();
			trx.commit();
			commited = true;
		} catch (NotSupportedException | SystemException
				| IllegalStateException | SecurityException
				| HeuristicMixedException | HeuristicRollbackException
				| RollbackException e) {
			throw new RuntimeException(e);
		} finally {
			if (!commited) {
				try {
					trx.rollback();
				} catch (IllegalStateException | SecurityException
						| SystemException e) {
					log.error("Error during rollback", e);
				}
			}
		}
	}

	public ActionResult getErrorDestination() {
		return errorDestination;
	}

	public void setErrorDestination(ActionResult target) {
		errorDestination = target;

	}

	public String getDestinationUrl() {
		return destinationUrl;
	}

	public void setDestinationUrl(String destinationUrl) {
		this.destinationUrl = destinationUrl;

	}
}
