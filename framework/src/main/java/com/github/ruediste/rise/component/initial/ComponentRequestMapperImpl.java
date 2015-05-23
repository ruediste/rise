package com.github.ruediste.rise.component.initial;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.IControllerComponent;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.RequestMapperBase;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.mvc.MvcRequestParseResult;

/**
 * Registers the {@link ControllerMvc}s with the {@link PathInfoIndex} during
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
        super(IControllerComponent.class);
    }

    @Override
    protected RequestParseResult createParseResult(ActionInvocation<String> path) {
        return new MvcRequestParseResult(path, actionInvocation -> {
            componentRequestInfo.setComponentRequest(true);
            requestInfo.setStringActionInvocation(actionInvocation);
            componentConfig.handleInitialRequest();
        });
    }
}
