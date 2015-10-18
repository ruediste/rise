package com.github.ruediste.rise.core.web.assetPipeline;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.rise.core.CoreRestartableModule;
import com.github.ruediste.rise.core.CoreUtil;

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
    AssetHelper helper;

    @Inject
    CssProcessor cssProcessor;

    AttachedPropertyBearerBase cache = new AttachedPropertyBearerBase();

    final List<AssetBundleOutput> outputs = new ArrayList<>();

    private final Function<String, Asset> classPathFunc = this::loadAssetFromClasspath;

    /**
     * Generate the URL for an asset. This can be used to replace urls enclosed
     * for example in CSS files
     */
    public String url(Asset asset) {
        return helper.url(asset);
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

    /**
     * Map the path of an asset to the full resouce path to be used to load the
     * asset from the classpath. Rules see {@link #locations(String...)}
     */
    String calculateAbsoluteLocation(String location) {
        return AssetHelper.calculateAbsoluteLocation(location,
                pipelineConfiguration.assetBasePath, getClass());
    }

    Asset loadAssetFromClasspath(String path) {
        return helper.loadAssetFromClasspath(path, getClass());
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

    public AssetLocationGroup webJar(String name, String... locations) {
        String pomPropsLocation = "META-INF/maven/org.webjars/" + name
                + "/pom.properties";
        Properties pomProps = new Properties();
        InputStream in = getClass().getClassLoader()
                .getResourceAsStream(pomPropsLocation);
        if (in == null) {
            throw new RuntimeException(
                    "unable to find " + pomPropsLocation + " on classpath");
        }
        try {
            pomProps.load(in);
        } catch (IOException e) {
            throw new RuntimeException("error while loading " + pomPropsLocation
                    + " from classpath", e);
        }
        String version = pomProps.getProperty("version");
        String prefix = "/META-INF/resources/webjars/" + name + "/" + version
                + "/";
        return new AssetLocationGroup(this,
                Arrays.stream(locations).map(x -> prefix + x));
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

    public AssetGroup join(AssetGroup... groups) {
        return new AssetGroup(this,
                Arrays.stream(groups).flatMap(g -> g.assets.stream()));
    }
}
