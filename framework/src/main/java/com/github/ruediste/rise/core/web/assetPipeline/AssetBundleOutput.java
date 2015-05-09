package com.github.ruediste.rise.core.web.assetPipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Collects {@link Asset} from an {@link AssetBundle}. The
 * {@link AssetRenderUtil} can be used to reference the assets of an output from
 * a page.
 */
public class AssetBundleOutput extends AssetGroup implements Consumer<Asset> {

	public AssetBundleOutput(AssetBundleBase bundle) {
		super(bundle, new ArrayList<>());
		bundle.registerOutput(this);
	}

	@Override
	public void accept(Asset t) {
		assets.add(t);
	}

	public void accept(AssetGroup group) {
		group.assets.forEach(this);
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public void clear() {
		assets.clear();
	}

}
