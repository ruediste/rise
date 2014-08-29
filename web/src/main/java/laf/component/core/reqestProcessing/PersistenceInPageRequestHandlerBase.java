package laf.component.core.reqestProcessing;

import javax.inject.Inject;

import laf.component.core.DelegatingRequestHandler;
import laf.core.base.*;
import laf.core.persistence.LafPersistenceContextManager;

public class PersistenceInPageRequestHandlerBase<T> extends
		DelegatingRequestHandler<T, T> {

	@Inject
	LafLogger log;

	@Inject
	LafPersistenceContextManager manager;

	@Inject
	PageScopedPersistenceHolder holder;

	@Override
	public ActionResult handle(final T path) {
		final Val<ActionResult> result = new Val<>();

		manager.withPersistenceHolder(holder, new Runnable() {

			@Override
			public void run() {
				result.set(getDelegate().handle(path));
			}
		});

		return result.get();
	}

}
