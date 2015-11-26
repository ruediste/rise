package com.github.ruediste.rise.component.initial;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.ContentRenderResult;
import com.github.ruediste.rise.core.web.HttpServletResponseCustomizer;

public class ViewRenderer extends ChainedRequestHandler {

    @Inject
    ComponentPage componentPage;

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
        ComponentPage page = componentPage.self();
        page.setView(config.createView(page.getController()));
        ViewComponentBase<?> view = page.getView();
        coreRequestInfo.setActionResult(new ContentRenderResult(
                util.renderComponents(page, view.getRootComponent()), r -> {
                    r.setContentType(coreConfiguration.htmlContentType);
                    if (view instanceof HttpServletResponseCustomizer) {
                        ((HttpServletResponseCustomizer) view)
                                .customizeServletResponse(r);
                    }
                }));
    }
}
