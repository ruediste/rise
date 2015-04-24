package com.github.ruediste.laf.core.web.assetPipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Collects {@link Asset} from an {@link AssetBundle}. The
 * {@link AssetRenderUtil} can be used to reference the assets of an output from
 * a page.
 */
public class AssetBundleOutput implements Consumer<Asset> {

	private final List<Asset> assets = new ArrayList<>();

	public AssetBundleOutput(AssetBundle bundle) {
		bundle.registerOutput(this);
	}

	@Override
	public void accept(Asset t) {
		assets.add(t);
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public void clear() {
		assets.clear();
	}

	public void forEach(Consumer<? super Asset> action) {
		assets.stream().forEach(action);
	}

}
