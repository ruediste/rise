package laf.mvc;

import javax.inject.Inject;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import laf.base.ActionResult;
import laf.core.actionPath.ActionPath;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.core.persistence.LafPersistenceContextManager;
import laf.core.requestProcessing.DelegatingRequestProcessor;

/**
 * Controller managing transactions and entity managers
 */
public class MvcPersistenceRequestProcessor extends DelegatingRequestProcessor {

	@Inject
	UserTransaction transaction;

	@Inject
	LafPersistenceContextManager contextManager;

	@Inject
	MvcService mvcService;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> actionPath) {
		boolean updating = mvcService.isUpdating(actionPath.getLast()
				.getMethodInfo());
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

				return getDelegate().process(actionPath);
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
