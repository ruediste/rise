package com.github.ruediste.rise.integration;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.id;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.internal.CharactersWriteable;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.api.ViewMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.integration.PageRenderer.PageRendererParameters;

public abstract class StageRibbonControllerBase<TSelf extends StageRibbonControllerBase<TSelf>>
		extends ControllerMvc<TSelf> {

	private static class Data {
		Class<? extends StageRibbonControllerBase<?>> controllerClass;
		public String originPathInfo;
		public AssetBundleOutput assets;
	}

	private static class View extends
			ViewMvc<StageRibbonControllerBase<?>, Data> {

		@Inject
		PageRenderer renderer;

		@Override
		public void render(HtmlCanvas html) throws IOException {
			setControllerClass(data().controllerClass);
			renderer.renderOn(html, new PageRendererParameters() {








				@Override
				protected void renderJsLinks(HtmlCanvas html)
						throws IOException {
					html.render(jsLinks(data().assets));
				}

				@Override
				protected void renderHead(HtmlCanvas html) throws IOException {
					html.title().content("Stage Ribbon");
				}

				@Override
				protected void renderCssLinks(HtmlCanvas html)
						throws IOException {
					html.render(cssLinks(data().assets));

				}

				@Override
				protected void renderBody(HtmlCanvas html) throws IOException {
					// @formatter:off
					html
					.div(class_("container"))
						.div(class_("row"))
							.div(class_("col-xs-12"))
								.h1(class_("text-center")).content("System Admin")
							._div()
						._div()
						.div(class_("row"))
							.div(class_("col-xs-12 col-sm-6 text-center"))
								.a(class_("btn btn-primary")
										.href(url(new PathInfo(data().originPathInfo)))).span(class_("glyphicon glyphicon-arrow-left"))._span().content("Go Back")
							._div()
							.div(class_("col-xs-12 col-sm-6 text-center"))
								.a(class_("btn btn-danger")
										.href(url(new PathInfo(data().originPathInfo)))).span(class_("glyphicon glyphicon-refresh"))._span().content("Drop-and-Create Database")
							._div()
						._div()	
					._div();
					// @formatter:on
				}

				@Override
				protected CharactersWriteable htmlAttributes() {
					return id("page-rise-ribbon");
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	public ActionResult index(String originPathInfo) {
		Data data = new Data();
		data.controllerClass = (Class<? extends StageRibbonControllerBase<?>>) getClass();
		data.originPathInfo = originPathInfo;
		data.assets = getAssets();
		return view(View.class, data);
	}

	protected abstract AssetBundleOutput getAssets();
}
