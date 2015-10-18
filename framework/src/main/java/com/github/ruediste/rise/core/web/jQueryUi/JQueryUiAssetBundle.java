package com.github.ruediste.rise.core.web.jQueryUi;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;

public class JQueryUiAssetBundle extends AssetBundle {
    public AssetGroup out;

    @Override
    protected void initialize() {

        out = webJar("jquery-ui", "jquery-ui.js", "jquery-ui.css",
                "jquery-ui.theme.css", "jquery-ui.structure.css")
                        .insertMinInProd().load();
    }

}
