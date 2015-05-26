package com.github.ruediste.rise.sample;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.integration.PageTemplateBase;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;

public class SamplePageTemplate extends PageTemplateBase {

    @Inject
    SampleBundle bundle;

    @Inject
    RisePageTemplate risePageTemplate;

    public void renderOn(HtmlCanvas canvas,
            SamplePageTemplateParameters parameters) throws IOException {
        // your drawing code
        risePageTemplate.renderOn(canvas, new RisePageTemplateParameters() {

            @Override
            protected void renderJsLinks(HtmlCanvas html) throws IOException {
                html.render(jsLinks(bundle.out));
            }

            @Override
            protected void renderCssLinks(HtmlCanvas html) throws IOException {
                html.render(cssLinks(bundle.out));
            }

            @Override
            protected void renderHead(HtmlCanvas html) throws IOException {
                // YOUR DRAWING CODE
            }

            @Override
            protected void renderBody(HtmlCanvas html) throws IOException {
                // YOUR DRAWING CODE
            }
        });
    }

    public interface SamplePageTemplateParameters {
        // flags and callback methods
    }
}
