package com.github.ruediste.rise.testApp;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.bootstrap.BootstrapBundleUtil;

public class TestAssetBundle extends AssetBundle {

    @Inject
    CoreAssetBundle coreBundle;

    @Inject
    BootstrapBundleUtil bootstrap;

    AssetBundleOutput out = new AssetBundleOutput(this);

    @Override
    protected void initialize() {
        coreBundle.out.send(out);
        bootstrap.loadAssets().all().send(out);
    }

}
