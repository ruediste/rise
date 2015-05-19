package com.github.ruediste.rise.sample.db;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.api.ViewMvc;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.mvc.IControllerMvc;

public abstract class PageView<TController extends IControllerMvc, TData>
		extends ViewMvc<TController, TData> {

	private static class Bundle extends AssetBundle {

		AssetBundleOutput out = new AssetBundleOutput(this);

	}

	@Inject
	Bundle bundle;

	@Override
	public void render(HtmlCanvas html) throws IOException {
		//@formatter:off
		html.html()
			.head()
				.render(cssLinks(bundle.out))
				.render(jsLinks(bundle.out))
			._head()
			.body();
				renderBody(html);
			html._body()
		._html();
	}

	protected abstract void renderBody(HtmlCanvas html) throws IOException ;

}
