package com.github.ruediste.rise.core.web.assetPipeline;

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

}
