package com.github.ruediste.rise.mvc;

import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.RequestMapperBase;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.RequestParseResultImpl;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;

/**
 * Registers the {@link ControllerMvc}s with the {@link PathInfoIndex} during
 * {@link #initialize()} and supports URL generation by providing
 * {@link #generate(ActionInvocation, Supplier)}
 */
public class MvcRequestMapperImpl extends RequestMapperBase {
    @Inject
    CoreRequestInfo requestInfo;

    @Inject
    MvcConfiguration mvcWebConfig;

    public MvcRequestMapperImpl() {
        super(IControllerMvc.class);
    }

    @Override
    protected RequestParseResult createParseResult(ActionInvocation<String> path) {
        return new RequestParseResultImpl(path, actionInvocation -> {
            requestInfo.setStringActionInvocation(actionInvocation);
            mvcWebConfig.handleRequest();
        });
    }
}
