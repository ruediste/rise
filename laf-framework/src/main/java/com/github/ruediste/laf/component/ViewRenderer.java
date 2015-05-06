package com.github.ruediste.laf.component;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ChainedRequestHandler;
import com.github.ruediste.laf.core.CoreRequestInfo;
import com.github.ruediste.laf.core.web.ContentRenderResult;

public class ViewRenderer extends ChainedRequestHandler {

	@Inject
	PageInfo pageInfo;

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	ComponentUtil util;

	@Override
	public void run(Runnable next) {
		next.run();
		coreRequestInfo.setActionResult(new ContentRenderResult(util
				.renderComponents(pageInfo.getView(), pageInfo.getView()
						.getRootComponent())));
	}
}
