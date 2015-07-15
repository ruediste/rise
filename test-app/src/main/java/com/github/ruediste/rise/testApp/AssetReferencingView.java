package com.github.ruediste.rise.testApp;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;

public class AssetReferencingView extends
        ViewMvc<AssetReferencingController, String> {

    static class Bundle extends AssetBundle {

        AssetBundleOutput out = new AssetBundleOutput(this);

        @Override
        public void initialize() {
            locations("./assetReferencing.css", ".-test.css",
                    "/assetReferencing/test.css", "assetReferencing.css")
                    .load().send(out);
        }

    }

    @Inject
    Bundle bundle;

    @Override
    public void render(TestCanvas html) {
        // @formatter:off
		html.html()
			.head()
				.rCssLinks(bundle.out)
			._head()
			.body()
				.div().ID("data").content(data())
				.div().ID("samePackage").content("foo")
				.div().ID("bundleClass").content("foo")
				.div().ID("default").content("foo")
				.div().ID("absolute").content("foo")
			._body()
		._html();
	}

}
