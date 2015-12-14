package com.github.ruediste.rise.testApp;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ViewMvcBase;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.mvc.IControllerMvc;
import com.github.ruediste.rise.testApp.TestPageTemplate.TestPageTemplateParameters;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

public abstract class ViewMvc<TController extends IControllerMvc, TData>
        extends ViewMvcBase<TController, TData, TestCanvas> {

    @Inject
    TestPageTemplate template;

    @Inject
    LabelUtil labelUtil;

    @Override
    protected void render(TestCanvas html) {
        template.renderOn(html, new TestPageTemplateParameters() {

            @Override
            public void renderContent(TestCanvas html) {
                ViewMvc.this.renderContent(html);
            }

            @Override
            public LString getTitle() {
                return ViewMvc.this.getTitle();
            }

            @Override
            public AssetBundleOutput getAssetBundleOut(MvcAssetBundle bundle) {
                return ViewMvc.this.getAssetBundleOut(bundle);
            }
        });

    }

    protected AssetBundleOutput getAssetBundleOut(MvcAssetBundle bundle) {
        return bundle.out;
    }

    protected abstract void renderContent(TestCanvas html);

    protected LString getTitle() {
        return labelUtil.getTypeLabel(getClass());
    }
}
