package com.github.ruediste.rise.core.web.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.ruediste.rise.core.web.assetPipeline.Asset;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;
import com.github.ruediste.rise.nonReloadable.front.reload.Reloadable;

@Reloadable
public class BootstrapBundleUtil extends AssetBundle {

    public static class BootstrapAssetGroups {
        public AssetGroup out;
        /**
         * The required fonts. Do NOT modify the names of these assets any
         * further, as their URLs are already inserted into the css. Just send
         * them to an output.
         */
        public AssetGroup fonts;
        public AssetGroup theme;

        public AssetGroup all() {
            return withoutFonts().join(fonts);
        }

        public AssetGroup withoutFonts() {
            return out.join(theme);
        }
    }

    /**
     * Load the necessary assets to use bootstrap, including jquery
     */
    public BootstrapAssetGroups loadAssets() {
        return loadAssets(
                (group, ext) -> group.ifProd(g -> g.name("{hash}." + ext)));
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
        AssetGroup js = locations("./js/bootstrap.js").load();
        ArrayList<Asset> fonts = new ArrayList<>();
        AssetGroup css = replaceFonts(fonts, fontCustomizer,
                locations("./css/bootstrap.css").insertMinInProd().load(),
                "eot", "svg", "ttf", "woff", "woff2");
        result.out = css.join(js).ifDev(
                g -> g.join(locations("./css/bootstrap.css.map").load()));
        result.fonts = new AssetGroup(this, fonts);
        result.theme = locations("./css/bootstrap-theme.css").load();

        return result;
    }

    private AssetGroup replaceFonts(List<Asset> fonts,
            BiFunction<AssetGroup, String, AssetGroup> fontCustomizer,
            AssetGroup css, String... fontExts) {
        AssetGroup group = css.mapData(new Function<String, String>() {
            @Override
            public String apply(String content) {
                String result = content;
                for (String ext : fontExts) {
                    result = result.replace(
                            "../fonts/glyphicons-halflings-regular." + ext,
                            url(loadFont(fontCustomizer, ext)
                                    .fork(g -> g.forEach(fonts::add))
                                    .single()));
                }
                return result;
            }

            @Override
            public String toString() {
                return "replaceFonts()";
            }
        }).cache();
        // eagerly load the data, such that the fonts get loaded and added to
        // the fonts list
        group.assets.forEach(a -> a.getData());
        return group;
    }

    private AssetGroup loadFont(
            BiFunction<AssetGroup, String, AssetGroup> fontCustomizer,
            String ext) {
        return fontCustomizer.apply(
                locations("./fonts/glyphicons-halflings-regular." + ext).load(),
                ext);
    }

    @Override
    protected void initialize() {
    }

}
