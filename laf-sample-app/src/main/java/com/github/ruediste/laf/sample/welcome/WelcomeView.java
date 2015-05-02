package com.github.ruediste.laf.sample.welcome;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.laf.sample.db.TodoController;

public class WelcomeView extends
		ViewMvcWeb<WelcomeController, WelcomeController.Data> {

	static class Bundle extends AssetBundle {

		AssetBundleOutput out = new AssetBundleOutput(this);

		@Override
		public void initialize() {
			paths("/assets/welcome.css").load().send(out);
		}

	}

	@Inject
	Bundle bundle;

	@Override
	public void render(HtmlCanvas html) throws IOException {
		//@formatter:off
		html.html().head().render(cssBundle(bundle.out))._head()
		.body()
			.h1().content("Hello Nina")
			.a(href(url(path().other()))).content("other")
			.a(href(url(path(TodoController.class).index()))).content("Todo Items")
		._body()._html();
	}

}
