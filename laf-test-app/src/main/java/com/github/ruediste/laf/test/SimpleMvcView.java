package com.github.ruediste.laf.test;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;

public class SimpleMvcView extends ViewMvcWeb<String> {

	static class Bundle extends AssetBundle {

		AssetBundleOutput out = new AssetBundleOutput(this);

		@Override
		public void initialize() {
			paths("./simple.css").load().send(out);
		}

	}

	@Override
	public void render(HtmlCanvas html) throws IOException {
		html.html().body().write(data())._body()._html();
	}

}
