package com.github.ruediste.laf.core.web.assetPipeline;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import com.github.ruediste.laf.core.ProjectStage;

@Singleton
public class AssetPipelineConfiguration {

	/**
	 * Map a file extension (without leading period) to an {@link AssetType}
	 */
	public final Map<String, AssetType> extensionToAssetTypeMap = new HashMap<>();

	/**
	 * get an asset type by a file extension
	 */
	public AssetType getType(String extension) {
		return extensionToAssetTypeMap.get(extension);
	}

	/**
	 * Map an {@link AssetType} to a file extension (without leading period)
	 */
	public final Map<AssetType, String> assetTypeToExtensionMap = new HashMap<>();

	public String getExtension(AssetType type) {
		return assetTypeToExtensionMap.get(type);
	}

	/**
	 * Map an {@link AssetType} to the corresponding content type
	 */
	public final Map<AssetType, String> assetTypeToDefaultContentTypeMap = new HashMap<>();

	public String getDefaultContentType(AssetType type) {
		return assetTypeToDefaultContentTypeMap.get(type);
	}

	{
		extensionToAssetTypeMap.put("js", DefaultAssetTypes.JS);
		assetTypeToExtensionMap.put(DefaultAssetTypes.JS, "js");
		assetTypeToDefaultContentTypeMap.put(DefaultAssetTypes.JS,
				"application/javascript; ; charset=UTF-8");

		extensionToAssetTypeMap.put("css", DefaultAssetTypes.CSS);
		assetTypeToExtensionMap.put(DefaultAssetTypes.CSS, "css");
		assetTypeToDefaultContentTypeMap.put(DefaultAssetTypes.CSS,
				"text/css; ; charset=UTF-8");

	}

	public AssetMode assetMode;

	@PostConstruct
	private void setupAssetMode(ProjectStage stage) {
		if (stage == ProjectStage.DEVELOPMENT)
			assetMode = AssetMode.DEVELOPMENT;
		else
			assetMode = AssetMode.PRODUCTION;
	}

	public AssetMode getAssetMode() {
		return assetMode;
	}

	/**
	 * Prefix of the path info for assets (in the URL)
	 */
	public String assetPathInfoPrefix = "/assets";

	/**
	 * Prefix when loading assets from the file system
	 */
	public String assetFilePathPrefix = "/WEB_INF/assets";

	/**
	 * Prefix when loading assets from the classpath
	 */
	public String classPathPrefix = "";
}
