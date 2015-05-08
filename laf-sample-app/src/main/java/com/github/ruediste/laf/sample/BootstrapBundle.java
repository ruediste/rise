package com.github.ruediste.laf.sample;

import javax.inject.Inject;

import com.github.ruediste.laf.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.laf.core.web.bootstrap.BootstrapBundleUtil;
import com.github.ruediste.laf.core.web.bootstrap.BootstrapBundleUtil.BootstrapAssetGroups;

public class BootstrapBundle extends AssetBundle {

	public AssetBundleOutput out = new AssetBundleOutput(this);

	@Inject
	BootstrapBundleUtil bootstrapUtil;

	@Override
	public void initialize() {
		BootstrapAssetGroups bootstrapAssets = bootstrapUtil.loadAssets();
		bootstrapAssets.out.send(out);
		bootstrapAssets.fonts.send(out);
	}
}
