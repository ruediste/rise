package com.github.ruediste.rise.component.initial;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.PageInfo;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.ContentRenderResult;

public class ViewRenderer extends ChainedRequestHandler {

    @Inject
    PageInfo pageInfo;

    @Inject
    CoreRequestInfo coreRequestInfo;

    @Inject
    ComponentUtil util;

    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    ComponentConfiguration config;

    @Override
    public void run(Runnable next) {
        next.run();
        PageInfo pi = pageInfo;
        pi.setView(config.createView(pi.getController()));
        coreRequestInfo
                .setActionResult(new ContentRenderResult(util.renderComponents(
                        pi.getView(), pi.getView().getRootComponent()),
                        coreConfiguration.htmlContentType));
    }
}
