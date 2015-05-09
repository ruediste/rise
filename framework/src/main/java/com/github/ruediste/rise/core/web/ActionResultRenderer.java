package com.github.ruediste.rise.core.web;

import java.io.IOException;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreRequestInfo;

public class ActionResultRenderer extends ChainedRequestHandler {

	@Inject
	CoreRequestInfo coreInfo;

	@Inject
	HttpRenderResultUtil util;

	@Override
	public void run(Runnable next) {
		next.run();
		try {
			coreInfo.getActionResult().sendTo(coreInfo.getServletResponse(),
					util);
		} catch (IOException e) {
			throw new RuntimeException("Error while sending result", e);
		}
	}
}