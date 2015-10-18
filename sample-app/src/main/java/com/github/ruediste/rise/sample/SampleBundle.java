package com.github.ruediste.rise.sample;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;
import com.github.ruediste.rise.core.web.assetPipeline.CssProcessor;
import com.github.ruediste.rise.core.web.assetPipeline.DefaultAssetTypes;
import com.github.ruediste.rise.core.web.bootstrap.BootstrapBundle;
import com.github.ruediste.rise.core.web.fileinput.FileinputAssetBundle;
import com.github.ruediste.rise.core.web.jQuery.JQueryAssetBundle;
import com.github.ruediste.rise.core.web.jQueryUi.JQueryUiAssetBundle;

public class SampleBundle extends AssetBundle {

    public AssetBundleOutput out = new AssetBundleOutput(this);

    @Inject
    JQueryAssetBundle jQueryAssetBundle;

    @Inject
    JQueryUiAssetBundle jQueryUiAssetBundle;

    @Inject
    BootstrapBundle bootstrap;

    @Inject
    CoreAssetBundle core;

    @Inject
    FileinputAssetBundle fileinputAssetBundle;

    @Inject
    CssProcessor css;

    @Override
    public void initialize() {
        AssetGroup assets = join(jQueryAssetBundle.out, jQueryUiAssetBundle.out,
                bootstrap.out, core.out, fileinputAssetBundle.out,
                locations("/assets/welcome.css", "/assets/welcome.js").load()
                        .ifProd(g -> g.min()));

        assets.select(DefaultAssetTypes.CSS)
                .split(css.process("{name}{hash}.{extT}",
                        a -> a.name("{ext}/{name}-{hash}.{ext}")))
                .ifProd(g -> g
                        .select(DefaultAssetTypes.CSS, DefaultAssetTypes.JS)
                        .split(g1 -> g1.combine().min()
                                .name("all-{hash}.{extT}")))
                .send(out);
    }
}
