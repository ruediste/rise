package laf.component.core;

import laf.base.ActionResult;

public interface ControllerUtil {

	void commit();

	void commit(Runnable inTransaction);

	void checkAndCommit(Runnable checker);

	void checkAndCommit(Runnable checker, Runnable inTransaction);

	void setErrorDestination(ActionResult target);

	void setDestination(ActionResult target);

}
