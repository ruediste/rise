package com.github.ruediste.laf.component;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.ruediste.laf.core.ChainedRequestHandler;
import com.github.ruediste.salta.standard.util.SimpleProxyScopeHandler;

public class PageCreationHandler extends ChainedRequestHandler {

	@Inject
	@Named("pageScoped")
	SimpleProxyScopeHandler pageScopeHandler;

	@Inject
	PageInfo pageInfo;

	@Override
	public void run(Runnable next) {
		pageScopeHandler.enter();
		try {
			pageInfo.setValueMap(pageScopeHandler.getValueMap());
			next.run();
		} finally {
			pageScopeHandler.exit();
		}
	}

}
