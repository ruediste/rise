package com.github.ruediste.rise.core.web;

import javax.annotation.PostConstruct;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetGroup;

public class CoreAssetBundle extends AssetBundle {

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

	@PostConstruct
	public void initialize() {
		out = paths("./core.js").load();
	}

}
