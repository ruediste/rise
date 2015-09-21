package com.github.ruediste.rise.core.web.assetPipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;

import ro.isdc.wro.extensions.processor.css.Less4jProcessor;
import ro.isdc.wro.extensions.processor.css.SassCssProcessor;
import ro.isdc.wro.extensions.processor.css.YUICssCompressorProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;

import com.github.ruediste.rise.nonReloadable.NonRestartable;
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
    public AssetType getDefaultAssetType(String extension) {
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

    public void registerExtension(AssetType assetType, String primaryExtension,
            String... otherExtensions) {
        assetTypeToExtensionMap.put(assetType, primaryExtension);
        extensionToAssetTypeMap.put(primaryExtension, assetType);
        for (String extension : otherExtensions) {
            assetTypeToExtensionMap.put(assetType, extension);
        }
    }

    {
        registerExtension(DefaultAssetTypes.JS, "js");
        assetTypeToDefaultContentTypeMap.put(DefaultAssetTypes.JS,
                "application/javascript; ; charset=UTF-8");

        registerExtension(DefaultAssetTypes.CSS, "css");
        assetTypeToDefaultContentTypeMap.put(DefaultAssetTypes.CSS,
                "text/css; ; charset=UTF-8");

        registerExtension(DefaultAssetTypes.LESS, "less");
        registerExtension(DefaultAssetTypes.SASS, "sass");
    }

    /**
     * Map extensions (without leading dot) to content types
     */
    public final Map<String, String> contentTypeMap = new HashMap<>();

    {

    }

    /**
     * Get the content type for an extension. After checking
     * {@link #contentTypeMap}, the configured asset types are taken into
     * account
     * 
     * @param extension
     *            extension without leading dot (for example "css","jpg","js")
     */
    public String getContentType(String extension) {
        String result = contentTypeMap.get(extension);
        if (result == null) {
            result = getDefaultContentType(getDefaultAssetType(extension));
        }
        return result;
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
    public String assetPathInfoPrefix = "/assets/";

    @Inject
    @NonRestartable
    ServletConfig servletConfig;

    /**
     * Prefix when loading assets from the classpath
     */
    public String assetBasePath = "assets/";

    public void initialize() {

    }

    public final Map<AssetType, Function<Asset, Asset>> defaultProcessors = new HashMap<>();

    public Function<Asset, Asset> getDefaultProcessor(AssetType type) {
        return defaultProcessors.get(type);
    }

    public final Map<AssetType, Function<Asset, Asset>> defaultMinifiers = new HashMap<>();

    public Function<Asset, Asset> getDefaultMinifier(AssetType type) {
        return defaultMinifiers.get(type);
    }

    @PostConstruct
    void initializeProcessors(Provider<ProcessorWrapper> processorSupplier) {
        defaultProcessors.put(DefaultAssetTypes.LESS,
                processorSupplier.get().initialize(DefaultAssetTypes.CSS,
                        () -> new Less4jProcessor()));
        defaultProcessors.put(DefaultAssetTypes.SASS,
                processorSupplier.get().initialize(DefaultAssetTypes.CSS,
                        () -> new SassCssProcessor()));
        defaultMinifiers.put(DefaultAssetTypes.CSS,
                processorSupplier.get().initialize(DefaultAssetTypes.CSS,
                        () -> new YUICssCompressorProcessor()));
        defaultMinifiers.put(DefaultAssetTypes.JS, processorSupplier.get()
                .initialize(DefaultAssetTypes.JS, () -> new JSMinProcessor()));
    }
}
