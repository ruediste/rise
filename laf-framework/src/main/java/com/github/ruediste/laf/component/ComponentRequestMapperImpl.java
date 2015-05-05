package com.github.ruediste.laf.component;

import javax.inject.Inject;

import com.github.ruediste.laf.api.ControllerMvcWeb;
import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.core.RequestMapperBase;
import com.github.ruediste.laf.core.RequestParseResult;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.mvc.web.MvcWebRequestParseResult;

/**
 * Registers the {@link ControllerMvcWeb}s with the {@link PathInfoIndex} during
 * {@link #initialize()} and supports URL generation by providing
 * {@link #generate(ActionInvocation)}
 */
public class ComponentRequestMapperImpl extends RequestMapperBase {

	@Inject
	ComponentRequestInfo requestInfo;

	@Inject
	ComponentConfiguration componentConfig;

	public ComponentRequestMapperImpl() {
		super(IComponentController.class);
	}

	@Override
	protected RequestParseResult createParseResult(ActionInvocation<String> path) {
		return new MvcWebRequestParseResult(path, actionInvocation -> {
			requestInfo.setStringActionInvocation(actionInvocation);
			componentConfig.handleRequest();
		});
	}
}
