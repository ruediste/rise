package laf.component.reqestProcessing;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.base.*;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.persistence.LafPersistenceContextManager;
import laf.requestProcessing.DelegatingRequestProcessor;

public class PersistenceInPageRequestProcessor extends
		DelegatingRequestProcessor {

	@Inject
	LafLogger log;

	@Inject
	LafPersistenceContextManager manager;

	@Inject
	PageScopedPersistenceHolder holder;

	@Override
	public ActionResult process(final ActionPath<ParameterValueProvider> path) {
		final Val<ActionResult> result = new Val<>();

		manager.withPersistenceHolder(holder, new Runnable() {

			@Override
			public void run() {
				result.set(getDelegate().process(path));
			}
		});

		return result.get();
	}

}
