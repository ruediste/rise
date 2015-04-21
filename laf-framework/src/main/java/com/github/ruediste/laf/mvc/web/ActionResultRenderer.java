package com.github.ruediste.laf.mvc.web;

import java.io.IOException;

import javax.inject.Inject;

import com.github.ruediste.laf.core.CoreRequestInfo;
import com.github.ruediste.laf.core.web.HttpRenderResultUtil;

public class ActionResultRenderer extends ChainedRequestHandler {
	@Inject
	MvcWebRequestInfo info;

	@Inject
	CoreRequestInfo coreInfo;

	@Inject
	HttpRenderResultUtil util;

	@Override
	public void run(Runnable next) {
		next.run();
		try {
			info.getActionResult().sendTo(coreInfo.getServletResponse(), util);
		} catch (IOException e) {
			throw new RuntimeException("Error while sending result", e);
		}
	}
}