package com.github.ruediste.rise.integration;

import javax.inject.Inject;

import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.AssetRequestMapper;
import com.github.ruediste.rise.core.web.assetPipeline.DefaultAssetTypes;

public class RiseCanvasHelper {

    @Inject
    private CoreUtil util;

    @Inject
    AssetRequestMapper mapper;

    public void rCssLinks(RiseCanvas<?> html, AssetBundleOutput output) {
        output.forEach(asset -> {
            if (asset.getAssetType() != DefaultAssetTypes.CSS)
                return;
            html.link().REL("stylesheet").TYPE("text/css")
                    .HREF(mapper.getPathInfo(asset));
        });
    }

    public void rJsLinks(RiseCanvas<?> html, AssetBundleOutput output) {
        output.forEach(asset -> {
            if (asset.getAssetType() != DefaultAssetTypes.JS)
                return;
            html.script().SRC(util.url(mapper.getPathInfo(asset)))._script();
        });
    }

    public CoreUtil getUtil() {
        return util;
    }
}
