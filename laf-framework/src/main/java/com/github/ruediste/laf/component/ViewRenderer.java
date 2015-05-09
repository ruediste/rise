package com.github.ruediste.laf.component;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ChainedRequestHandler;
import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.CoreRequestInfo;
import com.github.ruediste.laf.core.web.ContentRenderResult;

public class ViewRenderer extends ChainedRequestHandler {

	@Inject
	PageInfo pageInfo;

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	ComponentUtil util;

	@Inject
	CoreConfiguration coreConfiguration;

	@Inject
	ComponentConfiguration config;

	@Override
	public void run(Runnable next) {
		next.run();
		PageInfo pi = pageInfo;
		pi.setView(config.createView(pi.getController()));
		coreRequestInfo
				.setActionResult(new ContentRenderResult(util.renderComponents(
						pi.getView(), pi.getView().getRootComponent()),
						coreConfiguration.htmlContentType));
	}
}
