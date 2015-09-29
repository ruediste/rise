package com.github.ruediste.rise.sample;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.bootstrap.BootstrapBundleUtil;
import com.github.ruediste.rise.core.web.bootstrap.BootstrapBundleUtil.BootstrapAssetGroups;
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
    BootstrapBundleUtil bootstrapUtil;

    @Inject
    CoreAssetBundle core;

    @Inject
    FileinputAssetBundle fileinputAssetBundle;

    @Override
    public void initialize() {
        jQueryAssetBundle.out.send(out);
        jQueryUiAssetBundle.out.send(out);
        BootstrapAssetGroups bootstrap = bootstrapUtil.loadAssets();
        bootstrap.fonts.send(out);
        //@formatter:off
        bootstrap.withoutFonts()
                .join(core.out, fileinputAssetBundle.out)
                .join(locations("/assets/welcome.css", "/assets/welcome.js").load())
                .ifProd(g -> g.combine().min().name("{hash}.{extT}"))
                .send(out);
    }
}
