package com.github.ruediste.rise.mvc.web;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerMvcWeb;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.RequestMapperBase;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;

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
