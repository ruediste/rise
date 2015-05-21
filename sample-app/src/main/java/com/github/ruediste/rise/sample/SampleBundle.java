package com.github.ruediste.rise.sample;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.bootstrap.BootstrapBundleUtil;

public class SampleBundle extends AssetBundle {

	public AssetBundleOutput out = new AssetBundleOutput(this);

	@Inject
	BootstrapBundleUtil bootstrapUtil;

	@Inject
	CoreAssetBundle core;

	@PostConstruct
	public void initialize() {
		bootstrapUtil.loadAssets().sentAllTo(out);
		core.out.send(out);
		paths("/assets/welcome.css", "/assets/welcome.js").load().send(out);
	}
}
