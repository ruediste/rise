package laf.component.core;

import laf.base.ActionResult;

public class ControllerUtilImpl implements ControllerUtil {

	private ActionResult errorDestination;
	private ActionResult destination;
	private PagePersistenceManager pagePersistenceManager;

	public void initialize(PagePersistenceManager pagePersistenceManager) {
		this.pagePersistenceManager = pagePersistenceManager;

	}

	@Override
	public void commit() {
		pagePersistenceManager.commit(null, null);
	}

	@Override
	public void commit(Runnable inTransaction) {
		pagePersistenceManager.commit(null, inTransaction);
	}

	@Override
	public void checkAndCommit(Runnable checker) {
		pagePersistenceManager.commit(checker, null);
	}

	@Override
	public void checkAndCommit(Runnable checker, Runnable inTransaction) {
		pagePersistenceManager.commit(checker, inTransaction);
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
