package com.github.ruediste.rise.component.initial;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.github.ruediste.rendersnakeXT.canvas.ByteArrayHtmlConsumer;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentRequestInfo;
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

        // apply constraint violations to view
        componentRequestInfo
                .forEachInitialConstraintViolation(new ComponentRequestInfo.InitialConstraintViolationConsumer() {

                    @Override
                    public void accept(Object controller, Set<ConstraintViolation<?>> violations) {
                        ((SubControllerComponent) controller).applyConstraintViolations(violations);
                    }
                });

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
