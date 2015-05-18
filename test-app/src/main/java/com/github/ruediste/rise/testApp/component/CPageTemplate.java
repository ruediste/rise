package com.github.ruediste.rise.testApp.component;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.template.ComponentTemplateBase;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.bootstrap.BootstrapBundleUtil;
import com.github.ruediste.rise.core.web.bootstrap.BootstrapBundleUtil.BootstrapAssetGroups;
import com.github.ruediste.rise.integration.PageRenderer;

public class CPageTemplate extends ComponentTemplateBase<CPage> {

	private static class Bundle extends AssetBundle {
		@Inject
		CoreAssetBundle coreBundle;

		@Inject
		BootstrapBundleUtil bootstrapBundleUtil;

		AssetBundleOutput out = new AssetBundleOutput(this);

		@PostConstruct
		public void initialize() {
			BootstrapAssetGroups group = bootstrapBundleUtil.loadAssets();
			group.fonts.send(out);
			group.out.send(out);
			coreBundle.out.send(out);
		}

	}

	private static class TestPageRenderer extends PageRenderer<CPage> {
		@Inject
		Bundle bundle;

		@Inject
		ComponentUtil util;

		@Override
		protected void renderCssLinks(HtmlCanvas html, CPage data)
				throws IOException {

		}

		@Override
		protected void renderJsLinks(HtmlCanvas html, CPage data)
				throws IOException {

		}

		@Override
		protected void renderHead(HtmlCanvas html, CPage data)
				throws IOException {
		}

		@Override
		protected void renderBody(HtmlCanvas html, CPage data)
				throws IOException {
			for (Component child : data.getChildren())
				util.render(child, html);

			html.render(util.jsLinks(bundle.out));
		}

	}

	@Inject
	TestPageRenderer renderer;

	@Override
	public void render(CPage component, HtmlCanvas html) throws IOException {
		renderer.renderOn(html, component);
	}
}
