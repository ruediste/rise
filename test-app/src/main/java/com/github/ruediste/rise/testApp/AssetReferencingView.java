package com.github.ruediste.rise.testApp;

import static org.rendersnake.HtmlAttributesFactory.id;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.api.ViewMvcWeb;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;

public class AssetReferencingView extends
		ViewMvcWeb<AssetReferencingController, String> {

	static class Bundle extends AssetBundle {

		AssetBundleOutput out = new AssetBundleOutput(this);

		@Override
		public void initialize() {
			paths("./assetReferencing.css", ".-test.css",
					"/assetReferencing/test.css", "assetReferencing.css")
					.load().send(out);
		}

	}

	@Inject
	Bundle bundle;

	@Override
	public void render(HtmlCanvas html) throws IOException {
		// @formatter:off
		html.html()
			.head()
				.render(cssBundle(bundle.out))
			._head()
			.body()
				.div(id("data")).content(data())
				.div(id("samePackage")).content("foo")
				.div(id("bundleClass")).content("foo")
				.div(id("default")).content("foo")
				.div(id("absolute")).content("foo")
			._body()
		._html();
	}

}
