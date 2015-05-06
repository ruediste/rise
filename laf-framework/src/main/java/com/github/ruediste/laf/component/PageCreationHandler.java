package com.github.ruediste.laf.component;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.ruediste.laf.core.ChainedRequestHandler;
import com.github.ruediste.laf.core.CoreRequestInfo;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.util.SimpleProxyScopeHandler;

public class PageCreationHandler extends ChainedRequestHandler {

	@Inject
	@Named("pageScoped")
	SimpleProxyScopeHandler pageScopeHandler;

	@Inject
	PageInfo pageInfo;

	@Inject
	ComponentSessionInfo sessionInfo;

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	Injector injector;

	@Inject
	ComponentViewRepository repository;

	@Inject
	ComponentConfiguration config;

	@Override
	public void run(Runnable next) {
		pageScopeHandler.enter();
		try {
			PageInfo pi = pageInfo.self();
			synchronized (pi.getLock()) {
				pi.setValueMap(pageScopeHandler.getValueMap());
				pi.setPageId(sessionInfo.takePageId());

				Object controller = injector.getInstance(coreRequestInfo
						.getStringActionInvocation().methodInvocation
						.getInstanceClass());
				pi.setController((IComponentController) controller);

				pi.setView(config.createView(pi.getController()));
				next.run();
			}
		} finally {
			pageScopeHandler.exit();
		}
	}

}
