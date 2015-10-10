package com.github.ruediste.rise.core.web.jQuery;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;

public class JQueryAssetBundle extends AssetBundle {

    public AssetGroup out;

    @Override
    protected void initialize() {
        out = locations("./jquery-2.1.3.js").load();
    }
}