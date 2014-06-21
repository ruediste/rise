package laf.component.reqestProcessing;

import javax.inject.Inject;
import javax.transaction.*;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.base.Val;
import laf.component.pageScope.PageScoped;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.persistence.LafPersistenceContextManager;
import laf.persistence.LafPersistenceHolder;
import laf.requestProcessing.DelegatingRequestProcessor;

import org.apache.log4j.Logger;

public class PersistenceInitialRequestProcessor extends
DelegatingRequestProcessor {

	@Inject
	Logger log;

	@Inject
	LafPersistenceContextManager manager;

	@Inject
	@PageScoped
	LafPersistenceHolder holder;

	@Inject
	UserTransaction trx;

	@Override
	public ActionResult process(final ActionPath<ParameterValueProvider> path) {
		final Val<ActionResult> result = new Val<>();
		try {
			trx.begin();
			manager.withPersistenceHolder(holder, new Runnable() {

				@Override
				public void run() {
					result.set(getDelegate().process(path));
				}
			});
			trx.commit();
			return result.get();
		} catch (NotSupportedException | SystemException
				| IllegalStateException | SecurityException
				| HeuristicMixedException | HeuristicRollbackException
				| RollbackException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (trx.getStatus() == Status.STATUS_ACTIVE
						|| trx.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
					trx.rollback();
				}
			} catch (IllegalStateException | SecurityException
					| SystemException e) {
				log.error("error while rolling back transaction", e);
			}
		}
	}

}
