package com.github.ruediste.rise.core.web.assetPipeline;

import java.util.function.Function;

/**
 * Represents an asset of a web page (css, js, font ...)
 */
public interface Asset {

    /**
     * Get the name of this asset
     */
    String getName();

    /**
     * Get the type of this asset
     * 
     * @see DefaultAssetTypes
     */
    AssetType getAssetType();

    /**
     * Get the content type (mime type) of this asset.
     */
    String getContentType();

    /**
     * The contents of this asset
     */
    byte[] getData();

    /**
     * The location this asset was loaded from. Will be null after merging
     * assets
     */
    String getLocation();

    /**
     * The loader this asset was loaded with
     */
    Function<String, Asset> getLoader();

}
