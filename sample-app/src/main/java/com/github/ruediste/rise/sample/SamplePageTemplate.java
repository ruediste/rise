package com.github.ruediste.rise.sample;

import java.io.IOException;

import javax.inject.Inject;

import com.github.ruediste.rise.integration.PageTemplateBase;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;

public class SamplePageTemplate extends PageTemplateBase {

    @Inject
    SampleBundle bundle;

    @Inject
    RisePageTemplate<SampleCanvas> risePageTemplate;

    public void renderOn(SampleCanvas canvas,
            SamplePageTemplateParameters parameters) throws IOException {
        // your drawing code
        risePageTemplate.renderOn(canvas,
                new RisePageTemplateParameters<SampleCanvas>() {

                    @Override
                    protected void renderJsLinks(SampleCanvas html)
                            throws IOException {
                        html.rJsLinks(bundle.out);
                    }

                    @Override
                    protected void renderCssLinks(SampleCanvas html)
                            throws IOException {
                        html.rCssLinks(bundle.out);
                    }

                    @Override
                    protected void renderHead(SampleCanvas html)
                            throws IOException {
                        // YOUR DRAWING CODE
                    }

                    @Override
                    protected void renderBody(SampleCanvas html)
                            throws IOException {
                        // YOUR DRAWING CODE
                    }
                });
    }

    public interface SamplePageTemplateParameters {
        // flags and callback methods
    }
}
