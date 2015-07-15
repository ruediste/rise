package com.github.ruediste.rise.core.web.assetPipeline;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.rise.core.CoreRestartableModule;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.HttpService;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;

/**
 * Defines a set of {@link AssetBundleOutput}s and how {@link Asset}s are loaded
 * and passed to them.
 * <p>
 * {@link AssetBundle}s are always singletons. See
 * {@link CoreRestartableModule#registerAssetBundleScopeRule()}
 */
public abstract class AssetBundle {

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

    /**
     * Generate the URL for an asset. This can be used to replace urls enclosed
     * for example in CSS files
     */
    public String url(Asset asset) {
        return httpService.urlStatic(requestMapper.getPathInfo(asset));
    }

    @PostConstruct
    private void queueInitialization() {
        requestMapper.queueInitialization(this);
    }

    protected abstract void initialize();

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

    /**
     * Called from the constructor of {@link AssetBundleOutput} to register the
     * output with the bundle.
     */
    void registerOutput(AssetBundleOutput assetBundleOutput) {
        outputs.add(assetBundleOutput);
    }

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
     * asset from the classpath. Rules see {@link #locations(String...)}
     */
    String calculateAbsoluteLocation(String location) {
        return calculateAbsoluteLocation(location,
                pipelineConfiguration.assetBasePath, getClass());
    }

    /**
     * Map the path of an asset to the full resouce path to be used to load the
     * asset from the classpath. Rules see {@link #locations(String...)}
     */
    public static String calculateAbsoluteLocation(String location,
            String basePath, Class<?> cls) {
        if (location.startsWith("/"))
            return location.substring(1);
        if (location.startsWith("./"))
            return getPackageName(cls).replace('.', '/')
                    + location.substring(1);
        if (location.startsWith("."))
            return cls.getName().replace('.', '/') + location.substring(1);
        return basePath + location;
    }

    Function<AssetLocationGroup, AssetGroup> classPath() {

        return group -> new AssetGroup(this, group.getLocations().stream()
                .map(this::loadAssetFromClasspath));
    }

    private Asset loadAssetFromClasspath(String path) {
        AssetType type = pipelineConfiguration
                .getDefaultAssetType(getExtension(path));
        String fullPath = calculateAbsoluteLocation(path);
        return new Asset() {

            @Override
            public String getName() {
                return fullPath;
            }

            @Override
            public byte[] getData() {
                InputStream in = getClass().getClassLoader()
                        .getResourceAsStream(fullPath);
                if (in == null) {
                    throw new RuntimeException(
                            "Unable to find resource on classpath: " + fullPath
                                    + ", reference from "
                                    + AssetBundle.this.getClass().getName());
                }
                return toByteArray(in);
            }

            @Override
            public AssetType getAssetType() {
                return type;
            }

            @Override
            public String getContentType() {
                return pipelineConfiguration.getDefaultContentType(type);
            }

            @Override
            public String toString() {
                return "classpath(" + fullPath + ")";
            }
        };
    }

    /**
     * Entry point for the asset pipeline EDSL. The assets are loaded using the
     * following rules:
     * 
     * <ul>
     * <li>if the location starts with a `/`, the path is absolute, based on the
     * root of the classpath</li>
     * <li>if the location starts with a `./`, the path is interpreted relative
     * to the package the asset bundle is located in</li>
     * <li>if the location is starting with a `.`, the full name of the bundle
     * class is prepended to the path.</li>
     * <li>otherwise, the asset location is interpreted relative to the asset
     * base path configured in `AssetPipelineConfiguration#assetBasePath`. By
     * default, this is `/assets/`.</li>
     * 
     * </ul>
     */
    public AssetLocationGroup locations(String... locations) {
        return new AssetLocationGroup(this, Arrays.stream(locations));
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

    /**
     * true if the current {@link AssetMode} is development
     */
    public boolean dev() {
        return getAssetMode() == AssetMode.DEVELOPMENT;
    }

    public AssetPipelineConfiguration getPipelineConfiguration() {
        return pipelineConfiguration;
    }

    /**
     * true if the current {@link AssetMode} is production
     */
    public boolean prod() {
        return getAssetMode() == AssetMode.PRODUCTION;
    }

}
