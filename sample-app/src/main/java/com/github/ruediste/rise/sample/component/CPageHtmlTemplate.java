package com.github.ruediste.rise.sample.component;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.template.ComponentTemplateBase;
import com.github.ruediste.rise.integration.PageRenderer;
import com.github.ruediste.rise.sample.SampleBundle;

public class CPageHtmlTemplate extends ComponentTemplateBase<CPage> {

	static class Page extends PageRenderer<CPage> {
		@Inject
		ComponentUtil util;

		@Inject
		SampleBundle bundle;

		@Override
		protected void renderCssLinks(HtmlCanvas html, CPage component)
				throws IOException {
			html.render(util.cssLinks(bundle.out));
		}

		@Override
		protected void renderJsLinks(HtmlCanvas html, CPage component)
				throws IOException {
			html.render(util.jsLinks(bundle.out));
		}

		@Override
		protected void renderHead(HtmlCanvas html, CPage component)
				throws IOException {
			// Nothing yet
		}

		@Override
		protected void renderBody(HtmlCanvas html, CPage component)
				throws IOException {
			component.getChildren().forEach(child -> util.render(child, html));
		}
	}

	@Inject
	Page page;

	@Override
	public void render(CPage component, HtmlCanvas html) throws IOException {
		page.renderOn(html, component);
	}
}
