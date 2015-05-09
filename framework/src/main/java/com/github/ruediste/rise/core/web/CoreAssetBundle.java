package com.github.ruediste.rise.core.web;

import com.github.ruediste.rise.core.front.reload.DynamicSpace;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleNonRegistered;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;

@DynamicSpace
public class CoreAssetBundle extends AssetBundleNonRegistered {

	public static final String bodyAttributeRestartQueryUrl = "rise-restart-query-url";
	public static final String bodyAttributeRestartNr = "rise-restart-nr";
	public static final String bodyAttributeReloadUrl = "rise-reload-url";
	public static final String bodyAttributeAjaxUrl = "rise-ajax-url";
	public static final String bodyAttributePageNr = "rise-page-nr";

	public static final String componentAttributeNr = "rise-component-nr";

	/**
	 * Contains the necessary assets to use Rise.
	 */
	public AssetGroup out;

	@Override
	public void initialize() {
		out = paths("./core.js").load();
	}

}
