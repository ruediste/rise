package com.github.ruediste.laf.sample.component;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.ComponentUtil;
import com.github.ruediste.laf.component.web.components.CPage;
import com.github.ruediste.laf.component.web.components.template.CWTemplateBase;
import com.github.ruediste.laf.sample.SampleBundle;
import com.github.ruediste.laf.sample.welcome.PageRenderable;

public class CPageHtmlTemplate extends CWTemplateBase<CPage> {

	static class Page extends PageRenderable<CPage> {
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
