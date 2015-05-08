package com.github.ruediste.laf.core.web;

import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleNonRegistered;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;

public class CoreAssetBundle extends AssetBundleNonRegistered {

	public static final String bodyAttributeRestartQueryUrl = "rise-restart-query-url";
	public static final String bodyAttributeRestartNr = "rise-restart-nr";

	public AssetBundleOutput out = new AssetBundleOutput(this);

	@Override
	public void initialize() {
		paths("./core.js").load().send(out);
	}

}
