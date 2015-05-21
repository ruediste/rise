package com.github.ruediste.rise.integration;

import static org.rendersnake.HtmlAttributesFactory.charset;
import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;
import static org.rendersnake.HtmlAttributesFactory.http_equiv;
import static org.rendersnake.HtmlAttributesFactory.name;

import java.io.IOException;
import java.util.function.Function;

import javax.inject.Inject;

import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;
import org.rendersnake.internal.CharactersWriteable;

import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.PageInfo;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.nonReloadable.front.RestartCountHolder;

public class PageRenderer {

	@Inject
	RestartCountHolder holder;

	@Inject
	CoreConfiguration coreConfig;

	@Inject
	ComponentConfiguration componentConfig;

	@Inject
	CoreUtil util;

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	ComponentRequestInfo componentRequestInfo;

	@Inject
	PageInfo pageInfo;

	public void renderOn(HtmlCanvas html, PageRendererParameters parameters)
			throws IOException {

		HtmlAttributes bodyAttributes = HtmlAttributesFactory.data(
				CoreAssetBundle.bodyAttributeRestartQueryUrl,
				util.url(coreConfig.restartQueryPathInfo)).data(
				CoreAssetBundle.bodyAttributeRestartNr,
				Long.toString(holder.get()));
		if (componentRequestInfo.isComponentRequest()) {
			bodyAttributes = bodyAttributes
					.data(CoreAssetBundle.bodyAttributePageNr,
							Long.toString(pageInfo.getPageId()))
					.data(CoreAssetBundle.bodyAttributeReloadUrl,
							util.url(componentConfig.getReloadPath()))
					.data(CoreAssetBundle.bodyAttributeAjaxUrl,
							util.url(componentConfig.getAjaxPath()));
		}
		//@formatter:off
		html.write("<!DOCTYPE html>",false).html(parameters.htmlAttributes())
			.head();
		parameters.renderDefaultMetaTags(html);
			parameters.renderHead(html);
			parameters.renderCssLinks(html);
		html._head()
		.body(parameters.addBodyAttributes(bodyAttributes));
			parameters.renderBody(html);
			parameters.renderJsLinks(html);
		html._body()._html();
		//@formatter:on
	}

	public abstract static class PageRendererParameters {
		protected void renderDefaultMetaTags(HtmlCanvas html)
				throws IOException {
			html.meta(charset("UTF-8"))
					.meta(http_equiv("X-UA-Compatible").content("IE=edge"))
					.meta(name("viewport").content(
							"width=device-width, initial-scale=1"));
		}

		protected CharactersWriteable htmlAttributes() {
			return new HtmlAttributes();
		}

		/**
		 * Hook to add additional attributes to the body tag
		 */
		protected HtmlAttributes addBodyAttributes(HtmlAttributes attrs) {
			return attrs;
		}

		/**
		 * Render the CSS links of the resource bundle
		 */
		protected abstract void renderCssLinks(HtmlCanvas html)
				throws IOException;

		/**
		 * Render the JS links of the resource bundle
		 */
		protected abstract void renderJsLinks(HtmlCanvas html)
				throws IOException;

		/**
		 * Render content of the head tag
		 */
		protected abstract void renderHead(HtmlCanvas html) throws IOException;

		/**
		 * Render content of the body tag
		 */
		protected abstract void renderBody(HtmlCanvas html) throws IOException;
	}

	public Renderable stageRibbon(Function<String, ActionResult> urlPoducer) {
		return html -> {
			html.div(class_("rise-ribbon rise-ribbon-red"))
					.a(href(util.url(urlPoducer.apply(coreRequestInfo
							.getRequest().getPathInfo()))))
					.content("Development")._div();
		};
	}
}
