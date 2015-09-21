package com.github.ruediste.rise.testApp;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste1.i18n.label.Labeled;

@Labeled
public class AssetReferencingView
        extends ViewMvc<AssetReferencingController, String> {

    static class Bundle extends AssetBundle {

        AssetBundleOutput out = new AssetBundleOutput(this);

        @Override
        public void initialize() {
            locations("./assetReferencing.css", ".-test.css",
                    "/assetReferencing/test.css", "assetReferencing.css").load()
                            .send(out);
        }

    }

    @Inject
    Bundle bundle;

    @Override
    protected AssetBundleOutput getAssetBundleOut(TestAssetBundle bundle) {
        return this.bundle.out;
    }

    @Override
    public void renderContent(TestCanvas html) {
        // @formatter:off
		html
		.div().ID("data").content(data())
		.div().ID("samePackage").content("foo")
		.div().ID("bundleClass").content("foo")
		.div().ID("default").content("foo")
		.div().ID("absolute").content("foo");
	}

}
