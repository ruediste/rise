package com.github.ruediste.rise.sample.component;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.template.ComponentTemplateBase;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;
import com.github.ruediste.rise.sample.SampleBundle;

public class CPageHtmlTemplate extends ComponentTemplateBase<CPage> {

    @Inject
    SampleBundle bundle;

    @Inject
    RisePageTemplate page;

    @Override
    public void doRender(CPage component, HtmlCanvas html) throws IOException {
        page.renderOn(html, new RisePageTemplateParameters() {

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

            @Override
            protected void renderBody(HtmlCanvas html) throws IOException {
                component.getChildren().forEach(
                        child -> util.render(child, html));

            }
        });
    }
}
