package com.github.ruediste.laf.core.web.assetPipeline;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.laf.core.CoreUtil;
import com.github.ruediste.laf.core.HttpService;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;

/**
 * Defines a set of {@link AssetBundleOutput}s and how {@link Asset}s are loaded
 * and passed to them
 */
public abstract class AssetBundleBase {

	@Inject
	AssetPipelineConfiguration pipelineConfiguration;

	@Inject
	AssetRequestMapper requestMapper;

	@Inject
	CoreUtil coreUtil;

	@Inject
	HttpService httpService;

	AttachedPropertyBearerBase cache = new AttachedPropertyBearerBase();

	final List<AssetBundleOutput> outputs = new ArrayList<>();

	public String url(Asset asset) {
		return httpService.urlStatic(requestMapper.getPathInfo(asset));
	}

	public AssetMode getAssetMode() {
		return pipelineConfiguration.getAssetMode();
	}

	public void reset() {
		clearCache();
		outputs.forEach(x -> x.clear());
	}

	public void clearCache() {
		cache.getAttachedPropertyMap().clearAll();
	}

	void registerOutput(AssetBundleOutput assetBundleOutput) {
		outputs.add(assetBundleOutput);
	}

	public abstract void initialize();

	private String getExtension(String path) {
		return Iterables.getLast(Splitter.on('.').split(path));
	}

	private static String getPackageName(Class<?> cls) {
		String classname = cls.getName();
		int index = classname.lastIndexOf('.');
		if (index != -1)
			return classname.substring(0, index);
		return "";
	}

	/**
	 * Map the path of an asset to the full resouce path to be used to load the
	 * asset from the classpath. Rules see {@link #paths(String...)}
	 */
	String calculateFullPath(String assetPath) {
		if (assetPath.startsWith("/"))
			return assetPath.substring(1);
		if (assetPath.startsWith("./"))
			return getPackageName(getClass()).replace('.', '/')
					+ assetPath.substring(1);
		if (assetPath.startsWith("."))
			return getClass().getName().replace('.', '/')
					+ assetPath.substring(1);
		return pipelineConfiguration.assetBasePath + assetPath;
	}

	Function<AssetPathGroup, AssetGroup> classPath() {

		return group -> new AssetGroup(this, group
				.getPaths()
				.stream()
				.<Asset> map(
						path -> {
							AssetType type = pipelineConfiguration
									.getDefaultType(getExtension(path));
							String fullPath = calculateFullPath(path);
							return new Asset() {

								@Override
								public String getName() {
									return fullPath;
								}

								@Override
								public byte[] getData() {
									InputStream in = getClass()
											.getClassLoader()
											.getResourceAsStream(fullPath);
									if (in == null) {
										throw new RuntimeException(
												"Unable to find resource on classpath: "
														+ fullPath
														+ ", reference from "
														+ AssetBundleBase.this
																.getClass()
																.getName());
									}
									return toByteArray(in);
								}

								@Override
								public AssetType getAssetType() {
									return type;
								}

								@Override
								public String getContentType() {
									return pipelineConfiguration
											.getDefaultContentType(type);
								}
							};
						}));
	}

	/**
	 * Entry point for the asset pipeline EDSL. The assets are loaded using the
	 * following rules:
	 * 
	 * <ul>
	 * <li>if the path starts with a `/`, the path is absolute, based on the
	 * root of the classpath</li>
	 * <li>if the path starts with a `./`, the path is interpreted relative to
	 * the package the asset bundle is located in</li>
	 * <li>if the path startign with a `.`, the full name of the bundle class is
	 * prepended to the path.</li>
	 * <li>otherwise, the asset path is interpreted relative to the asset base
	 * path configured in `AssetPipelineConfiguration#assetBasePath`. By
	 * default, this is `/assets/`.</li>
	 * 
	 * </ul>
	 */
	public AssetPathGroup paths(String... paths) {
		return new AssetPathGroup(this, Arrays.stream(paths));
	}

	private byte[] toByteArray(InputStream in) {
		try {
			byte[] bb = ByteStreams.toByteArray(in);
			in = null;
			return bb;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException(
							"unable to close stream used to load asset", e);
				}
		}
	}

	public boolean dev() {
		return getAssetMode() == AssetMode.DEVELOPMENT;
	}

	public boolean prod() {
		return getAssetMode() == AssetMode.PRODUCTION;
	}

}
