package com.github.ruediste.rise.sample.db;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.api.ViewMvc;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;
import com.github.ruediste.rise.mvc.IControllerMvc;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.sample.SampleBundle;
import com.github.ruediste.rise.sample.welcome.StageRibbonController;

public abstract class PageView<TController extends IControllerMvc, TData>
        extends ViewMvc<TController, TData> {

    @Inject
    SampleBundle bundle;

    @Inject
    RisePageTemplate risePageTemplate;

    @Override
    public void render(HtmlCanvas html) throws IOException {
        risePageTemplate.renderOn(html, new RisePageTemplateParameters() {

            @Override
            protected void renderJsLinks(HtmlCanvas html) throws IOException {
                html.render(jsLinks(bundle.out));

            }

            @Override
            protected void renderHead(HtmlCanvas html) throws IOException {

            }

            @Override
            protected void renderCssLinks(HtmlCanvas html) throws IOException {
                html.render(cssLinks(bundle.out));

            }

            @Inject
            ApplicationStage stage;

            @Override
            protected void renderBody(HtmlCanvas html) throws IOException {
                html.render(risePageTemplate.stageRibbon(true,
                        x -> go(StageRibbonController.class).index(x)));
                PageView.this.renderBody(html);
            }
        });
    }

    protected abstract void renderBody(HtmlCanvas html) throws IOException;

}
