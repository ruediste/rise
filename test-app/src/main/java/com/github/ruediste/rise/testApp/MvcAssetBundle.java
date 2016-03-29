package com.github.ruediste.rise.testApp;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.assetBundles.BootstrapBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.CssProcessor;
import com.github.ruediste.rise.core.web.assetPipeline.DefaultAssetTypes;

public class MvcAssetBundle extends AssetBundle {

    @Inject
    CoreAssetBundle coreBundle;

    @Inject
    BootstrapBundle bootstrap;

    @Inject
    CssProcessor css;

    AssetBundleOutput out = new AssetBundleOutput(this);

    @Override
    protected void initialize() {
        coreBundle.out.join(bootstrap.out).select(DefaultAssetTypes.CSS)
                .split(g -> g.map(css.process("{qname}.{ext}", x -> x))).send(out);
    }

}
