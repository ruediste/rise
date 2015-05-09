package com.github.ruediste.rise.component;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerMvcWeb;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.RequestMapperBase;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.mvc.web.MvcWebRequestParseResult;

/**
 * Registers the {@link ControllerMvcWeb}s with the {@link PathInfoIndex} during
 * {@link #initialize()} and supports URL generation by providing
 * {@link #generate(ActionInvocation)}
 */
public class ComponentRequestMapperImpl extends RequestMapperBase {

	@Inject
	CoreRequestInfo requestInfo;

	@Inject
	ComponentConfiguration componentConfig;

	@Inject
	ComponentRequestInfo componentRequestInfo;

	public ComponentRequestMapperImpl() {
		super(IComponentController.class);
	}

	@Override
	protected RequestParseResult createParseResult(ActionInvocation<String> path) {
		return new MvcWebRequestParseResult(path, actionInvocation -> {
			componentRequestInfo.setComponentRequest(true);
			requestInfo.setStringActionInvocation(actionInvocation);
			componentConfig.handleInitialRequest();
		});
	}
}
