package com.github.ruediste.rise.testApp.component;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.template.ComponentTemplateBase;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.bootstrap.BootstrapBundleUtil;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;

public class CPageTemplate extends ComponentTemplateBase<CPage> {

    private static class Bundle extends AssetBundle {
        @Inject
        CoreAssetBundle coreBundle;

        @Inject
        BootstrapBundleUtil bootstrapBundleUtil;

        AssetBundleOutput out = new AssetBundleOutput(this);

        @PostConstruct
        public void initialize() {
            bootstrapBundleUtil.loadAssets().sentAllTo(out);
            coreBundle.out.send(out);
        }

    }

    @Inject
    Bundle bundle;
    @Inject
    RisePageTemplate renderer;

    @Override
    public void doRender(CPage component, HtmlCanvas html) throws IOException {
        renderer.renderOn(html, new RisePageTemplateParameters() {

            @Override
            protected void renderJsLinks(HtmlCanvas html) throws IOException {
                html.render(util.jsLinks(bundle.out));

            }

            @Override
            protected void renderHead(HtmlCanvas html) throws IOException {

            }

            @Override
            protected void renderCssLinks(HtmlCanvas html) throws IOException {
                html.render(util.cssLinks(bundle.out));
            }

            @Override
            protected void renderBody(HtmlCanvas html) throws IOException {
                for (Component child : component.getChildren())
                    util.render(child, html);
            }
        });
    }
}
