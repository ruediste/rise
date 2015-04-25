package com.github.ruediste.laf.core.web.assetPipeline;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;

/**
 * Defines a set of {@link AssetBundleOutput}s and how {@link Asset}s are loaded
 * and passed to them
 */
public abstract class AssetBundle {

	@Inject
	AssetPipelineConfiguration pipelineConfiguration;

	AttachedPropertyBearerBase cache = new AttachedPropertyBearerBase();

	final List<AssetBundleOutput> outputs = new ArrayList<>();

	public AssetMode getAssetMode() {
		return pipelineConfiguration.getAssetMode();
	}

	public void reset() {
		cache.getAttachedPropertyMap().clearAll();
		outputs.forEach(x -> x.clear());
	}

	void registerOutput(AssetBundleOutput assetBundleOutput) {
		outputs.add(assetBundleOutput);
	}

	public abstract void initialize();

	private String getExtension(String path) {
		return Iterables.getLast(Splitter.on('.').split(path));
	}

	public Function<AssetPathGroup, AssetGroup> classPath() {

		return group -> new AssetGroup(this, group
				.getPaths()
				.stream()
				.<Asset> map(
						path -> {
							AssetType type = pipelineConfiguration
									.getDefaultType(getExtension(path));
							return new Asset() {

								@Override
								public String getName() {
									return path;
								}

								@Override
								public byte[] getData() {
									String fullPath = pipelineConfiguration.classPathPrefix
											+ (path.startsWith("/") ? path
													.substring(1) : path);
									InputStream in = getClass()
											.getClassLoader()
											.getResourceAsStream(fullPath);
									if (in == null) {
										throw new RuntimeException(
												"Unable to find resource on classpath: "
														+ fullPath
														+ ", reference from "
														+ AssetBundle.this
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
