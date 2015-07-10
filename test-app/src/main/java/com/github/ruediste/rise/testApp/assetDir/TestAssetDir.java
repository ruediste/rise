package com.github.ruediste.rise.testApp.assetDir;

import com.github.ruediste.rise.core.web.assetDir.AssetDir;

public class TestAssetDir extends AssetDir {

    @Override
    protected String getLocation() {
        return "./";
    }

    @Override
    protected String getName() {
        return "testDir";
    }

}
