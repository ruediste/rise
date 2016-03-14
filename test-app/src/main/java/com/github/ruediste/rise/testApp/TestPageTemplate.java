package com.github.ruediste.rise.testApp;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.integration.PageTemplateBase;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;
import com.github.ruediste1.i18n.lString.LString;

public class TestPageTemplate extends PageTemplateBase {

    @Inject
    MvcAssetBundle bundle;

    @Inject
    RisePageTemplate<TestCanvas> risePageTemplate;

    public void renderOn(TestCanvas canvas, TestPageTemplateParameters parameters) {
        // your drawing code
        // @formatter:off
        risePageTemplate.renderOn(canvas,
                new RisePageTemplateParameters<TestCanvas>() {

                    @Override
                    protected void renderJsLinks(TestCanvas html) {
                        html.rJsLinks(parameters.getAssetBundleOut(bundle));
                    }

                    @Override
                    protected void renderCssLinks(TestCanvas html) {
                        html.rCssLinks(parameters.getAssetBundleOut(bundle));
                    }

                    @Override
                    protected void renderHead(TestCanvas html) {
                        html.title().content(parameters.getTitle());
                    }

                    @Override
                    protected void renderBody(TestCanvas html) {
                        parameters.renderContent(html);
                    }
                });
        // @formatter:on
    }

    public interface TestPageTemplateParameters {
        LString getTitle();

        void renderContent(TestCanvas html);

        default AssetBundleOutput getAssetBundleOut(MvcAssetBundle bundle) {
            return bundle.out;
        }
    }
}
