package com.github.ruediste.rise.sample.welcome;

import static org.rendersnake.HtmlAttributesFactory.charset;
import static org.rendersnake.HtmlAttributesFactory.http_equiv;
import static org.rendersnake.HtmlAttributesFactory.name;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.PageInfo;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.front.ReloadCountHolder;
import com.github.ruediste.rise.core.web.CoreAssetBundle;

public abstract class PageRenderable<T> {

	@Inject
	ReloadCountHolder holder;

	@Inject
	CoreConfiguration coreConfig;

	@Inject
	ComponentConfiguration componentConfig;

	@Inject
	CoreUtil util;

	@Inject
	ComponentRequestInfo componentRequestInfo;

	@Inject
	PageInfo pageInfo;

	public void renderOn(HtmlCanvas html, T data) throws IOException {

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
		html.write("<!DOCTYPE html>",false).html()
			.head();
			renderDefaultMetaTags(html,data);
			renderHead(html,data);
			renderCssLinks(html,data);
		html._head()
		.body(addBodyAttributes(bodyAttributes,data));
			renderBody(html,data);
			renderJsLinks(html,data);
		html._body()._html();
	}

	private void renderDefaultMetaTags(HtmlCanvas html, T data)
			throws IOException {
		html.meta(charset("UTF-8"))
				.meta(http_equiv("X-UA-Compatible").content("IE=edge"))
				.meta(name("viewport").content(
						"width=device-width, initial-scale=1"));
	}

	/**
	 * Hook to add additional attributes to the body tag
	 */
	protected HtmlAttributes addBodyAttributes(HtmlAttributes attrs, T data) {
		return attrs;
	}

	/**
	 * Render the CSS links of the resource bundle
	 */
	protected abstract void renderCssLinks(HtmlCanvas html, T data)
			throws IOException;

	/**
	 * Render the JS links of the resource bundle
	 */
	protected abstract void renderJsLinks(HtmlCanvas html, T data)
			throws IOException;

	/**
	 * Render content of the head tag
	 */
	protected abstract void renderHead(HtmlCanvas html, T data)
			throws IOException;

	/**
	 * Render content of the body tag
	 */
	protected abstract void renderBody(HtmlCanvas html, T data)
			throws IOException;
}
