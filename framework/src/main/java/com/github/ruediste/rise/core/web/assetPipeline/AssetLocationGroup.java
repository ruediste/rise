package com.github.ruediste.rise.core.web.assetPipeline;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A group of locations of {@link Asset}s. Does NOT specify where the assets are
 * loaded from (class path, file system ...)
 */
public class AssetLocationGroup {
    private final AssetBundle bundle;
    private final List<String> locations;

    public AssetLocationGroup(AssetBundle bundle, String... locations) {
        this.bundle = bundle;
        this.locations = Arrays.asList(locations);
    }

    public AssetLocationGroup(AssetBundle bundle, Stream<String> locations) {
        this.bundle = bundle;
        this.locations = locations.collect(toList());
    }

    public List<String> getLocations() {
        return Collections.unmodifiableList(locations);
    }

    /**
     * Merge the locations in this group with the locations in the other group
     */
    public AssetLocationGroup merge(AssetLocationGroup other) {
        return new AssetLocationGroup(bundle, Stream.concat(locations.stream(),
                other.getLocations().stream()));
    }

    public AssetLocationGroup insertMinInProd() {
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
    public AssetLocationGroup insertMin() {
        return map(this::insertMin);
    }

    /**
     * Load the resources from the classpath
     */
    public AssetGroup load() {
        return new AssetGroup(bundle,
                locations.stream().map(bundle::loadAssetFromClasspath));
    }

    /**
     * Modify all paths in this group
     */
    public AssetLocationGroup map(Function<String, String> mapper) {
        return new AssetLocationGroup(bundle,
                getLocations().stream().map(mapper));
    }

    /**
     * Only keep the paths matching the filter in the location group.
     */
    public AssetLocationGroup filter(Predicate<String> filter) {
        return new AssetLocationGroup(bundle,
                getLocations().stream().filter(filter));
    }

    String insertMin(String location) {
        String[] parts = location.split("\\.");
        if (parts.length == 1) {
            return location;
        } else {
            return Arrays.asList(parts).subList(0, parts.length - 1).stream()
                    .collect(Collectors.joining(".")) + ".min."
                    + parts[parts.length - 1];
        }
    }

}
