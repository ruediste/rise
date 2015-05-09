package com.github.ruediste.laf.core;

import javax.inject.Inject;

import com.github.ruediste.laf.core.web.assetPipeline.AssetPipelineConfiguration;
import com.github.ruediste.laf.core.web.assetPipeline.AssetRequestMapper;
import com.github.ruediste.laf.util.Initializer;

public class CoreDynamicInitializer implements Initializer {

	@Inject
	CoreConfiguration config;

	@Inject
	AssetPipelineConfiguration pipelineConfig;

	@Inject
	AssetRequestMapper assetRequestMapper;

	@Inject
	PathInfoIndex index;

	@Inject
	RestartQueryParser restartQueryParser;

	@Override
	public void initialize() {
		config.dynamicClassLoader = Thread.currentThread()
				.getContextClassLoader();
		config.initialize();
		pipelineConfig.initialize();
		assetRequestMapper.initialize();

		index.registerPathInfo(config.restartQueryPathInfo.getValue(),
				restartQueryParser);
	}
}
