package com.github.ruediste.laf.core.web.assetPipeline;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A group of paths of {@link Asset}s. Does NOT specify where the assets are
 * loaded from (class path, file system ...)
 */
public class AssetPathGroup {
	private final AssetBundle bundle;
	private final Set<String> paths;

	public AssetPathGroup(AssetBundle bundle, String... paths) {
		this.bundle = bundle;
		this.paths = new HashSet<>(Arrays.asList(paths));
	}

	public AssetPathGroup(AssetBundle bundle, Stream<String> paths) {
		this.bundle = bundle;
		this.paths = paths.collect(toSet());
	}

	public Set<String> getPaths() {
		return Collections.unmodifiableSet(paths);
	}

	/**
	 * Merge the paths in this group with the paths in the other group
	 */
	public AssetPathGroup merge(AssetPathGroup other) {
		return new AssetPathGroup(bundle, Stream.concat(paths.stream(), other
				.getPaths().stream()));
	}

	public AssetPathGroup insertMinInProd() {
		if (bundle.getAssetMode() == AssetMode.PRODUCTION) {
			return insertMin();
		} else {
			return this;
		}
	}

	/**
	 * Insert <b>.min.</b> before the extension. For example <b>jquery.js</b>
	 * becomes <b> jquery.min.js </b>
	 * 
	 */
	public AssetPathGroup insertMin() {
		return map(this::insertMin);
	}

	/**
	 * Load the resources from the classpath
	 */
	public AssetGroup load() {
		return bundle.classPath().apply(this);
	}

	/**
	 * Modify all paths in this group
	 */
	public AssetPathGroup map(Function<String, String> mapper) {
		return new AssetPathGroup(bundle, getPaths().stream().map(mapper));
	}

	/**
	 * Only keep the paths matching the filter in the path group.
	 */
	public AssetPathGroup filter(Predicate<String> filter) {
		return new AssetPathGroup(bundle, getPaths().stream().filter(filter));
	}

	String insertMin(String path) {
		String[] parts = path.split("\\.");
		if (parts.length == 1) {
			return path;
		} else {
			return Arrays.asList(parts).subList(0, parts.length - 1).stream()
					.collect(Collectors.joining("."))
					+ ".min." + parts[parts.length - 1];
		}
	}

}
