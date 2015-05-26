package com.github.ruediste.rise.sample.component;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.template.ComponentTemplateBase;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;
import com.github.ruediste.rise.sample.SampleBundle;
import com.github.ruediste.rise.sample.SampleCanvas;

public class CPageHtmlTemplate extends ComponentTemplateBase<CPage> {

    @Inject
    SampleBundle bundle;

    @Inject
    RisePageTemplate<SampleCanvas> page;

    @Override
    public void doRender(CPage component, HtmlCanvas html) {
        page.renderOn(html, new RisePageTemplateParameters<SampleCanvas>() {

            @Override
            protected void renderJsLinks(SampleCanvas html) {
                html.rJsLinks(bundle.out);
            }

            @Override
            protected void renderHead(SampleCanvas html) {

            }

            @Override
            protected void renderCssLinks(SampleCanvas html) {
                html.rCssLinks(bundle.out);
            }

            @Override
            protected void renderBody(SampleCanvas html) {
                component.getChildren().forEach(
                        child -> util.render(child, html));

            }
        });
    }
}
