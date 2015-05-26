package com.github.ruediste.rise.integration;

import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.DefaultAssetTypes;

public interface RiseCanvas<TSelf extends RiseCanvas<TSelf>> extends
        Html5Canvas<TSelf> {

    RiseCanvasHelper internal_riseHelper();

    /**
     * Render a css link for all {@link DefaultAssetTypes#CSS} assets in the
     * given output
     */
    default TSelf rCssLinks(AssetBundleOutput output) {
        internal_riseHelper().rCssLinks(this, output);
        return self();
    }

    /**
     * Render a js link for all {@link DefaultAssetTypes#JS} assets in the given
     * output
     */
    default TSelf rJsLinks(AssetBundleOutput output) {
        internal_riseHelper().rJsLinks(this, output);
        return self();
    }
}
