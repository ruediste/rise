package com.github.ruediste.rise.core;

import javax.inject.Inject;

import com.github.ruediste.rise.core.scopes.HttpScopeManager;
import com.github.ruediste.rise.core.scopes.SessionScopeEvents;
import com.github.ruediste.rise.core.web.assetDir.AssetDirRequestMapper;
import com.github.ruediste.rise.core.web.assetPipeline.AssetPipelineConfiguration;
import com.github.ruediste.rise.core.web.assetPipeline.AssetRequestMapper;
import com.github.ruediste.rise.util.Initializer;

public class CoreRestartableInitializer implements Initializer {

    @Inject
    CoreConfiguration config;

    @Inject
    AssetPipelineConfiguration pipelineConfig;

    @Inject
    AssetRequestMapper assetRequestMapper;

    @Inject
    AssetDirRequestMapper assetDirRequestMapper;

    @Inject
    PathInfoIndex index;

    @Inject
    RestartQueryParser restartQueryParser;

    @Inject
    HttpScopeManager httpScopeManager;

    @Inject
    SessionScopeEvents sessionScopeEvents;

    @Override
    public void initialize() {
        config.dynamicClassLoader = Thread.currentThread()
                .getContextClassLoader();
        config.initialize();
        pipelineConfig.initialize();
        assetRequestMapper.initialize();
        assetDirRequestMapper.initialize();

        index.registerPathInfo(config.restartQueryPathInfo.getValue(),
                restartQueryParser);
    }
}
