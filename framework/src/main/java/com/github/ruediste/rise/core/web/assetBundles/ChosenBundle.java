package com.github.ruediste.rise.core.web.assetBundles;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;

public class ChosenBundle extends AssetBundle {
    public AssetGroup out;

    @Override
    protected void initialize() {
        out = webJar("chosen", "chosen.jquery.min.js", "chosen.min.css").load();
    }

}
