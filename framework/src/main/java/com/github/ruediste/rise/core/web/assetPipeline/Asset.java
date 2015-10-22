package com.github.ruediste.rise.core.web.assetPipeline;

/**
 * Represents an asset of a web page (css, js, font ...)
 */
public interface Asset {

    /**
     * Return the absolute classpath resource location this asset was loaded
     * from. If the asset is the result of the combination of multiple
     * resources, null should be returnded.
     */
    String getClasspathLocation();

    /**
     * Get the name of this asset. It can be used together with the context path
     * to form an absolute URL.
     * 
     * <p>
     * If the returned string starts with a forward slash (/) the result is used
     * as is. Otherwise the value of
     * {@link AssetPipelineConfiguration#assetPathInfoPrefix} is prepended. This
     * can be achieved using
     * {@link AssetPipelineHelper#getAssetPathInfo(String)}
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
