package com.github.ruediste.rise.sample;

import javax.annotation.PostConstruct;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;

public class FaviconBundle extends AssetBundle {

    AssetBundleOutput out = new AssetBundleOutput(this);

    @PostConstruct
    public void initialize() {
        locations("favicon.ico").load().name("/favicon.ico").send(out);
    }
}
