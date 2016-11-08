package com.github.ruediste.rise.component.initial;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.rendersnakeXT.canvas.ByteArrayHtmlConsumer;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.ViewFactory;
import com.github.ruediste.rise.component.render.CanvasTargetFirstPass;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.ContentRenderResult;
import com.github.ruediste.rise.core.web.HttpServletResponseCustomizer;
import com.github.ruediste.rise.integration.RiseCanvas;

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
    ViewFactory viewFactory;

    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Inject
    Provider<CanvasTargetFirstPass> fistPassTargetProvider;

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
        ViewComponentBase<?> view = viewFactory.createView((SubControllerComponent) page.getController(), true, null);
        page.setView(view);

        // render result
        RiseCanvas<?> html = coreConfiguration.createApplicationCanvas();
        page.setCanvas(html);
        CanvasTargetFirstPass target = fistPassTargetProvider.get();
        target.captureStartStackTraces = coreConfiguration.doCaptureHtmlTagStartTraces();
        html.setTarget(target);
        target.setView(view);
        target.getRoot().setView(view);
        view.render(html);
        target.commitAttributes();
        target.flush();
        target.checkAllTagsClosed();
        page.setRoot(target.getRoot());

        ByteArrayHtmlConsumer consumer = new ByteArrayHtmlConsumer();
        target.getProducers().forEach(p -> p.produce(consumer));

        // set action result
        coreRequestInfo.setActionResult(new ContentRenderResult(consumer.getByteArray(), servletResponse -> {
            servletResponse.setContentType(coreConfiguration.htmlContentType);
            if (view instanceof HttpServletResponseCustomizer) {
                ((HttpServletResponseCustomizer) view).customizeServletResponse(servletResponse);
            }
        }));
    }
}
