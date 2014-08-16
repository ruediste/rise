package laf.component.reqestProcessing;

import javax.inject.Inject;

import laf.base.*;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.core.persistence.LafPersistenceContextManager;
import laf.core.requestProcessing.DelegatingRequestProcessor;
import laf.mvc.actionPath.ActionPath;

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
