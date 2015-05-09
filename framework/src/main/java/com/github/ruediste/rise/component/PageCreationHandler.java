package com.github.ruediste.rise.component;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreRequestInfo;
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
			PageHandle handle = sessionInfo.createPageHandle();
			handle.instances = pageScopeHandler.getValueMap();
			synchronized (handle.lock) {
				PageInfo pi = pageInfo.self();
				pi.setPageId(handle.id);

				Object controller = injector.getInstance(coreRequestInfo
						.getStringActionInvocation().methodInvocation
						.getInstanceClass());
				pi.setController((IComponentController) controller);

				next.run();
			}
		} finally {
			pageScopeHandler.exit();
		}
	}
}
