package com.github.ruediste.laf.core.web.assetPipeline;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;

/**
 * Defines a set of {@link AssetBundleOutput}s and how {@link Asset}s are loaded
 * and passed to them
 */
public class AssetBundle {

	@Inject
	AssetPipelineConfiguration pipelineConfiguration;

	AttachedPropertyBearerBase cache = new AttachedPropertyBearerBase();

	final List<AssetBundleOutput> outputs = new ArrayList<>();

	public AssetMode getAssetMode() {
		return pipelineConfiguration.getAssetMode();
	}

	public void clearCache() {
		cache.getAttachedPropertyMap().clearAll();
	}

	void registerOutput(AssetBundleOutput assetBundleOutput) {
		outputs.add(assetBundleOutput);
	}
}
