package com.github.ruediste.rise.core.web;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;
import com.github.ruediste.rise.core.web.assetPipeline.AssetRequestMapper;

public class CoreAssetBundle extends AssetBundle {

    public static final String bodyAttributeRestartQueryUrl = "rise-restart-query-url";
    public static final String bodyAttributeRestartNr = "rise-restart-nr";
    public static final String bodyAttributeReloadUrl = "rise-reload-url";
    public static final String bodyAttributeAjaxUrl = "rise-ajax-url";
    public static final String bodyAttributePageNr = "rise-page-nr";
    public static final String bodyAttributeReloadNr = "rise-reload-nr";

    public static final String componentAttributeNr = "rise-component-nr";

    /**
     * Contains the necessary assets to use Rise, besides JQuery. Note that
     * these assets will not be picked up by the {@link AssetRequestMapper}. You
     * need to send them to an {@link AssetBundleOutput}.
     */
    public AssetGroup out;

    @Override
    public void initialize() {
        out = locations("./core.js", "./core.css").load();
    }

}
