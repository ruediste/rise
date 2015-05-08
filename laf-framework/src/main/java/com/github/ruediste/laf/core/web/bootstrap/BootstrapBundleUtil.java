package com.github.ruediste.laf.core.web.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.github.ruediste.laf.core.web.assetPipeline.Asset;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleNonRegistered;
import com.github.ruediste.laf.core.web.assetPipeline.AssetGroup;

public class BootstrapBundleUtil extends AssetBundleNonRegistered {

	public static class BootstrapAssetGroups {
		public AssetGroup out;
		/**
		 * The required fonts. Do NOT modify the names of these assets any
		 * further, as their URLs are already inserted into the css. Just send
		 * them to an output.
		 */
		public AssetGroup fonts;
		public AssetGroup theme;
	}

	public BootstrapAssetGroups loadAssets() {
		return loadAssets((group, ext) -> group.forkJoin(
				g -> g.prod().name("{name}.{hash}.{ext}"),
				g -> g.dev().name("/fonts/{name}.{ext}")));
	}

	/**
	 * Load the necessary assets to use bootstrap
	 * 
	 * @param fontCustomizer
	 *            Hook to modify the font asset groups. Each passed group will
	 *            contain exactly one font asset. The string parameter contains
	 *            the extension of the font. After this processing step, the
	 *            font URL will be inserted into the bootstrap CSS file.
	 */
	public BootstrapAssetGroups loadAssets(
			BiFunction<AssetGroup, String, AssetGroup> fontCustomizer) {
		BootstrapAssetGroups result = new BootstrapAssetGroups();
		AssetGroup js = paths("./js/jquery-2.1.3.js", "./js/bootstrap.js")
				.insertMinInProd().load();
		ArrayList<Asset> fonts = new ArrayList<>();
		AssetGroup css = replaceFonts(fonts, fontCustomizer,
				paths("./css/bootstrap.css").insertMinInProd().load(), "eot",
				"svg", "ttf", "woff", "woff2");
		result.out = css.join(js);
		result.fonts = new AssetGroup(this, fonts);
		result.theme = paths("./css/bootstrap-theme.css").insertMinInProd()
				.load();

		return result;
	}

	private AssetGroup replaceFonts(List<Asset> fonts,
			BiFunction<AssetGroup, String, AssetGroup> fontCustomizer,
			AssetGroup css, String... fontExts) {
		return css.mapData(content -> {
			String result = content;
			for (String ext : fontExts) {
				AssetGroup font = loadFont(fontCustomizer, "eot").fork(
						g -> g.forEach(fonts::add));
				result = result.replace(
						"../fonts/glyphicons-halflings-regular." + ext,
						url(font.single()));
			}
			return result;
		});
	}

	private AssetGroup loadFont(
			BiFunction<AssetGroup, String, AssetGroup> fontCustomizer,
			String ext) {
		return fontCustomizer.apply(
				paths("./fonts/glyphicons-halflings-regular." + ext).load(),
				ext);
	}

	@Override
	public void initialize() {
		// NOP
	}

}
