package com.github.ruediste.rise.testApp;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.bootstrap.BootstrapBundle;

public class TestAssetBundle extends AssetBundle {

    @Inject
    CoreAssetBundle coreBundle;

    @Inject
    BootstrapBundle bootstrap;

    AssetBundleOutput out = new AssetBundleOutput(this);

    @Override
    protected void initialize() {
        coreBundle.out.send(out);
        bootstrap.out.send(out);
    }

}
