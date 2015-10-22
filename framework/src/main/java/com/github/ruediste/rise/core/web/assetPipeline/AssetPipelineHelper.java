package com.github.ruediste.rise.core.web.assetPipeline;

import javax.inject.Inject;

import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.web.PathInfo;

public class AssetPipelineHelper {

    @Inject
    AssetPipelineConfiguration pipelineConfiguration;

    @Inject
    CoreUtil util;

    public String getUrl(Asset asset) {
        return util.urlStatic(getPathInfo(asset));
    }

    public PathInfo getPathInfo(Asset asset) {
        return new PathInfo(getAssetPathInfo(asset.getName()));
    }

    public String getAssetPathInfo(String pathInfo) {
        if (pathInfo.startsWith("/"))
            return pathInfo;
        else
            return pipelineConfiguration.assetPathInfoPrefix + pathInfo;
    }
}
