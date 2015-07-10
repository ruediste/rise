package com.github.ruediste.rise.core.web.assetDir;

import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetPipelineConfiguration;

/**
 * Represents a directory containing assets.
 */
public abstract class AssetDir {
    String pathInfoPrefix;

    /**
     * The location of the asset directory. Return "./" for the directory
     * corresponding to the package the {@link AssetDir} is located in.
     * 
     * @see AssetBundle#locations(String...)
     */
    protected abstract String getLocation();

    /**
     * name of the final directory. If null is returned (the default), the
     * absolute location is used. The final {@link PathInfo} will be prefixed
     * with {@link AssetPipelineConfiguration#assetPathInfoPrefix}.
     */
    protected String getName() {
        return null;
    }

}
