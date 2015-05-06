package com.github.ruediste.laf.mvc.web;

import javax.inject.Inject;

import com.github.ruediste.laf.api.ControllerMvcWeb;
import com.github.ruediste.laf.core.CoreRequestInfo;
import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.core.RequestMapperBase;
import com.github.ruediste.laf.core.RequestParseResult;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;

/**
 * Registers the {@link ControllerMvcWeb}s with the {@link PathInfoIndex} during
 * {@link #initialize()} and supports URL generation by providing
 * {@link #generate(ActionInvocation)}
 */
public class MvcWebRequestMapperImpl extends RequestMapperBase {
	@Inject
	CoreRequestInfo requestInfo;

	@Inject
	MvcWebConfiguration mvcWebConfig;

	public MvcWebRequestMapperImpl() {
		super(IControllerMvcWeb.class);
	}

	@Override
	protected RequestParseResult createParseResult(ActionInvocation<String> path) {
		return new MvcWebRequestParseResult(path, actionInvocation -> {
			requestInfo.setStringActionInvocation(actionInvocation);
			mvcWebConfig.handleRequest();
		});
	}
}
