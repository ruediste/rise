package com.github.ruediste.rise.core.web.assetPipeline;

import static org.rendersnake.HtmlAttributesFactory.rel;
import static org.rendersnake.HtmlAttributesFactory.src;

import java.io.IOException;
import java.util.function.Function;

import javax.inject.Inject;

import org.rendersnake.Renderable;

import com.github.ruediste.rise.core.web.PathInfo;

public class AssetRenderUtil {
	@Inject
	AssetRequestMapper mapper;

	/**
	 * Render a css link for all {@link DefaultAssetTypes#CSS} assets in the
	 * given output
	 */
	public Renderable renderCss(Function<PathInfo, String> url,
			AssetBundleOutput output) {
		return html -> {
			output.forEach(asset -> {
				if (asset.getAssetType() != DefaultAssetTypes.CSS)
					return;
				try {
					html.link(rel("stylesheet").type("text/css").href(
							url.apply(mapper.getPathInfo(asset))));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		};
	}

	/**
	 * Renders a script link for all {@link DefaultAssetTypes#JS} assets in the
	 * given output
	 */
	public Renderable renderJs(Function<PathInfo, String> url,
			AssetBundleOutput output) {
		return html -> {
			output.forEach(asset -> {
				if (asset.getAssetType() != DefaultAssetTypes.JS)
					return;
				try {
					html.script(src(url.apply(mapper.getPathInfo(asset))))
							._script();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		};
	}
}
