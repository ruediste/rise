package com.github.ruediste.laf.core.web.assetPipeline;

import javax.annotation.PostConstruct;

/**
 * Base class for asset bundles which are not registered with the request
 * handler.
 */
public abstract class AssetBundleNonRegistered extends AssetBundleBase {

	@PostConstruct
	private void postConstruct() {
		initialize();
	}

}
