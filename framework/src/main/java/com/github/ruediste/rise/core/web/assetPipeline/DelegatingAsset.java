package com.github.ruediste.rise.core.web.assetPipeline;

import java.util.function.Function;

/**
 * Asset delegating to another. Can be used as base class if only one aspect of
 * an asset needs to be modified.
 */
public class DelegatingAsset implements Asset {

    @Override
    public String getLocation() {
        return delegate.getLocation();
    }

    @Override
    public Function<String, Asset> getLoader() {
        return delegate.getLoader();
    }

    private Asset delegate;

    public DelegatingAsset(Asset delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public AssetType getAssetType() {
        return delegate.getAssetType();
    }

    @Override
    public String getContentType() {
        return delegate.getContentType();
    }

    @Override
    public byte[] getData() {
        return delegate.getData();
    }
}
