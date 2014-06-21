package laf.component.reqestProcessing;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.base.Val;
import laf.component.pageScope.PageScoped;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.persistence.LafPersistenceContextManager;
import laf.persistence.LafPersistenceHolder;
import laf.requestProcessing.DelegatingRequestProcessor;

import org.apache.log4j.Logger;

public class PersistenceInPageRequestProcessor extends
DelegatingRequestProcessor {

	@Inject
	Logger log;

	@Inject
	LafPersistenceContextManager manager;

	@Inject
	@PageScoped
	LafPersistenceHolder holder;

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
