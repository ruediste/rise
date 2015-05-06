package com.github.ruediste.laf.component;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ChainedRequestHandler;

public class ViewRenderer extends ChainedRequestHandler {

	@Inject
	PageInfo pageInfo;

	@Override
	public void run(Runnable next) {
		next.run();

		pageInfo.getView().getRootComponent();
	}
}
