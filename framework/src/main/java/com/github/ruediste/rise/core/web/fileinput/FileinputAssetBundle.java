package com.github.ruediste.rise.core.web.fileinput;

import javax.annotation.PostConstruct;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;

public class FileinputAssetBundle extends AssetBundle {

    public AssetGroup out;

    @PostConstruct
    public void initialize() {
        out = locations("./css/fileinput.css", "./js/fileinput.js")
                .insertMinInProd().load();
    }
}
