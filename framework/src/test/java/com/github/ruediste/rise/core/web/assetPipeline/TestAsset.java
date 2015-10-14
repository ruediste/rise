package com.github.ruediste.rise.core.web.assetPipeline;

import com.google.common.base.Charsets;

class TestAsset implements Asset {

    private String name;
    private String data;

    public TestAsset(String name, String data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AssetType getAssetType() {
        return DefaultAssetTypes.CSS;
    }

    @Override
    public String getContentType() {
        return "text/css";
    }

    @Override
    public byte[] getData() {
        return data.getBytes(Charsets.UTF_8);
    }

    @Override
    public String toString() {
        return "testAsset(" + name + "," + data + ")";
    }

}