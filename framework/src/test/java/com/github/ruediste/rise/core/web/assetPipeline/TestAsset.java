package com.github.ruediste.rise.core.web.assetPipeline;

import com.google.common.base.Charsets;

class TestAsset implements Asset {

    private String pathInfo;
    private String data;

    public TestAsset(String pathInfo, String data) {
        this.pathInfo = pathInfo;
        this.data = data;
    }

    @Override
    public String getName() {
        return pathInfo;
    }

    @Override
    public String getClasspathLocation() {
        return "foo.css";
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
        return "testAsset(" + pathInfo + "," + data + ")";
    }

}