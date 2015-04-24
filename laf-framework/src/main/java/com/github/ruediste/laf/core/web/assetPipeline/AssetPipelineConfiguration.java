package com.github.ruediste.laf.core.web.assetPipeline;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.github.ruediste.laf.core.Permanent;
import com.github.ruediste.salta.standard.Stage;

@Singleton
public class AssetPipelineConfiguration {

	/**
	 * Map a file extension (without leading period) to an {@link AssetType}
	 */
	public final Map<String, AssetType> extensionToAssetTypeMap = new HashMap<>();

	/**
	 * get an asset type by a file extension
	 */
	public AssetType getDefaultType(String extension) {
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
	private void setupAssetMode(Stage stage) {
		if (stage == Stage.DEVELOPMENT)
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

	@Inject
	@Permanent
	ServletConfig servletConfig;

	/**
	 * Return the full file path prefix by resolving the
	 * {@link #assetFilePathPrefix} relative to the context root of this servlet
	 */
	public Path getAssetFilePathPrefix() {
		String path = servletConfig.getServletContext().getRealPath(
				assetFilePathPrefix);
		if (path == null)
			throw new RuntimeException("cannot find asset file path prefix "
					+ assetFilePathPrefix + " in the ServletContext");
		return Paths.get(path);
	}

	/**
	 * Open an input stream for an assetFile, loading from the
	 * {@link ServletContext#getResourceAsStream(String)}. Prepends
	 * {@link #assetFilePathPrefix}
	 */
	public InputStream getFileInputStream(String path) {
		return servletConfig.getServletContext().getResourceAsStream(
				assetFilePathPrefix + path);
	}

	/**
	 * Prefix when loading assets from the classpath
	 */
	public String classPathPrefix = "";

	public void initialize() {

	}
}
