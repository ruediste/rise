package com.github.ruediste.rise.component.initial;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.ByteArrayHtmlConsumer;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.fragment.HtmlFragmentUtil;
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

    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Override
    public void run(Runnable next) {
        ComponentPage page = componentPage.self();

        // execute rest of the chain
        next.run();

        // handle page closing in initial requests
        if (componentRequestInfo.getClosePageResult() != null) {
            coreRequestInfo.setActionResult(componentRequestInfo.getClosePageResult());
            return;
        }

        // create view
        ViewComponentBase<?> view = config.createView((SubControllerComponent) page.getController(), true);
        page.setView(view);

        // update structure
        HtmlFragmentUtil.updateStructure(view.getRootFragment());

        // render result
        ByteArrayHtmlConsumer consumer = new ByteArrayHtmlConsumer();
        view.getRootFragment().getHtmlProducer().produce(consumer);

        // set action result
        coreRequestInfo.setActionResult(new ContentRenderResult(consumer.getByteArray(), servletResponse -> {
            servletResponse.setContentType(coreConfiguration.htmlContentType);
            if (view instanceof HttpServletResponseCustomizer) {
                ((HttpServletResponseCustomizer) view).customizeServletResponse(servletResponse);
            }
        }));
    }
}
