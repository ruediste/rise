package com.github.ruediste.rise.core.web.bootstrap;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;

public class BootstrapBundle extends AssetBundle {

    public AssetGroup out;

    @Override
    protected void initialize() {
        out = webJar("bootstrap", "css/bootstrap.css", "js/bootstrap.js", "css/bootstrap-theme.css").insertMinInProd()
                .load();
    }
}
