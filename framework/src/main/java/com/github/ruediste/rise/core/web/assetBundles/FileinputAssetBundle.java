package com.github.ruediste.rise.core.web.assetBundles;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;

public class FileinputAssetBundle extends AssetBundle {

    public AssetGroup out;

    @Override
    public void initialize() {
        out = locations("./fileinput/css/fileinput.css", "./fileinput/js/fileinput.js", "./fileinput/riseFileinput.js")
                .load();
    }
}
