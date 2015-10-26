package com.github.ruediste.rise.core.web.assetPipeline;

import static java.util.stream.Collectors.minBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.web.assetPipeline.CssAnalyzer.CssProcessorHandler;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.rise.util.RiseUtil;
import com.google.common.base.Charsets;

/**
 * Processor to extract css image URLs and imports
 */
@Singleton
public class CssProcessor {

    @Inject
    AssetPipelineConfiguration config;

    @Inject
    CssAnalyzer analyzer;

    @Inject
    AssetHelper helper;

    private static class Ctx {
        Set<String> startedAssets = new HashSet<>();
        ArrayList<Asset> result = new ArrayList<>();
        Map<String, Asset> processedAssets = new HashMap<>();
        Function<Asset, Asset> referencedAssetProcessor;
        public String nameTemplate;
    }

    /**
     * Relocate the given style sheets without inlining imports
     * 
     * @param namePattern
     *            pattern to use as style sheet name (see
     *            {@link AssetGroup#name(String)})
     * @param referencedAssetProcessor
     *            processor for the referenced assets. The assets will be
     *            included in the returned assets. The same classpath location
     *            will only be processed once.
     */
    public Function<AssetGroup, AssetGroup> process(String namePattern,
            Function<AssetGroup, AssetGroup> referencedAssetProcessor) {
        return process(namePattern, referencedAssetProcessor, true);
    }

    /**
     * Relocate the given style sheets
     * 
     * @param namePattern
     *            pattern to use as style sheet name (see
     *            {@link AssetGroup#name(String)})
     * @param referencedAssetProcessor
     *            processor for the referenced assets. The assets will be
     *            included in the returned assets. The same classpath location
     *            will only be processed once.
     */
    public Function<AssetGroup, AssetGroup> process(String namePattern,
            Function<AssetGroup, AssetGroup> referencedAssetProcessor,
            boolean inline) {
        if (inline)
            return inlineStyleSheets(namePattern, referencedAssetProcessor);
        else
            return relocateStyleSheets(namePattern, referencedAssetProcessor);
    }

    /**
     * Relocate the given style sheets without inlining imports
     * 
     * @param namePattern
     *            pattern to use as style sheet name (see
     *            {@link AssetGroup#name(String)})
     * @param refencedAssetProcessor
     *            processor for the referenced assets. The assets will be
     *            included in the returned assets. The same classpath location
     *            will only be processed once.
     */
    public Function<AssetGroup, AssetGroup> relocateStyleSheets(
            String namePattern,
            Function<AssetGroup, AssetGroup> refencedAssetProcessor) {
        return group -> {
            Ctx ctx = new Ctx();
            ctx.referencedAssetProcessor = AssetGroup.toSingleAssetFunction(
                    group.bundle, refencedAssetProcessor);
            ctx.nameTemplate = namePattern;

            for (Asset style : group.assets) {
                String content = new String(style.getData(), Charsets.UTF_8);
                relocateStyleSheetImpl(ctx, content,
                        style.getClasspathLocation());
            }
            return new AssetGroup(group.bundle, ctx.result);
        };
    }

    private Asset relocateStyleSheetImpl(Ctx ctx, String content,
            String styleLocation) {
        Map<String, Asset> processedAssets = ctx.processedAssets;

        // check if the style sheet has already been processed
        {
            Asset result = processedAssets.get(styleLocation);
            if (result != null)
                return result;
        }

        // break endless loops
        if (!ctx.startedAssets.add(styleLocation)) {
            throw new RuntimeException(
                    "Import loop detected. Involved style sheets: "
                            + ctx.startedAssets);
        }

        // determine final name
        String targetStyleLocation = getTargetStyleLocation(ctx.nameTemplate,
                styleLocation);
        StringBuffer sb = new StringBuffer();
        analyzer.process(content, sb, new CssProcessorHandler() {

            @Override
            public boolean shouldInline(String ref, String media) {
                return false;
            }

            @Override
            public String replaceRef(String ref) {
                Optional<Pair<String, String>> splitRef = normalizeRef(ref);

                if (!splitRef.isPresent())
                    return ref;

                String location = RiseUtil.resolvePath(styleLocation,
                        splitRef.get().getA());
                Asset asset = processedAssets.get(location);
                if (asset == null) {
                    asset = helper.loadAssetFromClasspath(location,
                            () -> "style sheet " + styleLocation);
                    asset = ctx.referencedAssetProcessor.apply(asset);
                    processedAssets.put(location, asset);
                    ctx.result.add(asset);
                }
                return RiseUtil.getShortestPath(targetStyleLocation,
                        asset.getName()) + splitRef.get().getB();
            }

            @Override
            public String replaceImportRef(String ref) {
                String location = RiseUtil.resolvePath(styleLocation, ref);
                Asset asset = processedAssets.get(location);
                if (asset == null) {
                    byte[] refData = RiseUtil.readFromClasspath(location,
                            getClass().getClassLoader());
                    if (refData == null) {
                        throw new RuntimeException(
                                "Style sheet " + location + " imported from "
                                        + styleLocation + " not found");
                    }
                    asset = relocateStyleSheetImpl(ctx,
                            new String(refData, Charsets.UTF_8), location);
                    processedAssets.put(location, asset);
                }
                return RiseUtil.getShortestPath(targetStyleLocation,
                        asset.getName());
            }

            @Override
            public void performInline(StringBuffer sb, String ref,
                    String media) {
                throw new UnsupportedOperationException();
            }
        });
        ctx.startedAssets.remove(styleLocation);

        byte[] data = sb.toString().getBytes(Charsets.UTF_8);
        Asset result = createCssAsset(styleLocation, targetStyleLocation, data);
        processedAssets.put(styleLocation, result);
        ctx.result.add(result);
        return result;
    }

    private Optional<Pair<String, String>> normalizeRef(String ref) {
        Optional<Pair<String, String>> splitRef;
        if (ref.startsWith("data:") || ref.startsWith("http://")
                || ref.startsWith("https://"))
            splitRef = Optional.empty();
        else {
            int idx = Arrays
                    .asList(ref.indexOf('?'), ref.indexOf('#'), ref.length())
                    .stream().filter(x -> x >= 0)
                    .collect(minBy(Comparator.naturalOrder())).get();
            splitRef = Optional
                    .of(Pair.of(ref.substring(0, idx), ref.substring(idx)));
        }
        return splitRef;
    }

    private static class InlineCtx {
        Set<String> startedAssets = new HashSet<>();
        ArrayList<Asset> result = new ArrayList<>();
        Map<String, Asset> processedAssets = new HashMap<>();
        Function<Asset, Asset> referencedAssetProcessor;
        String nameTemplate;
        StringBuffer sb;
    }

    /**
     * Relocate the given style sheets without inlining imports
     * 
     * @param styleSheets
     *            the style sheets to relocate
     * @param nameTemplate
     *            pattern to use as style sheet name (see
     *            {@link AssetGroup#name(String)})
     * @param refencedAssetProcessor
     *            processor for the referenced assets. The assets will be
     *            included in the returned assets. The same classpath location
     *            will only be processed once.
     */
    public Function<AssetGroup, AssetGroup> inlineStyleSheets(
            String nameTemplate,
            Function<AssetGroup, AssetGroup> refencedAssetProcessor) {
        return assetGroup -> {
            InlineCtx ctx = new InlineCtx();
            ctx.referencedAssetProcessor = AssetGroup.toSingleAssetFunction(
                    assetGroup.bundle, refencedAssetProcessor);
            ctx.nameTemplate = nameTemplate;

            for (Asset style : assetGroup.assets) {
                ctx.sb = new StringBuffer();
                String content = new String(style.getData(), Charsets.UTF_8);
                String styleLocation = style.getName();
                String targetStyleLocation = getTargetStyleLocation(
                        ctx.nameTemplate, styleLocation);
                inlineStyleSheetImpl(ctx, content, styleLocation,
                        targetStyleLocation);

                byte[] data = ctx.sb.toString().getBytes(Charsets.UTF_8);
                Asset result = createCssAsset(style.getClasspathLocation(),
                        targetStyleLocation, data);
                ctx.processedAssets.put(styleLocation, result);
                ctx.result.add(result);
            }
            return new AssetGroup(assetGroup.bundle, ctx.result);
        };
    }

    private Asset createCssAsset(String classpathLocation, String name,
            byte[] data) {
        return new Asset() {

            @Override
            public String getClasspathLocation() {
                return classpathLocation;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public byte[] getData() {
                return data;
            }

            @Override
            public String getContentType() {
                return config.getDefaultContentType(DefaultAssetTypes.CSS);
            }

            @Override
            public AssetType getAssetType() {
                return DefaultAssetTypes.CSS;
            }

            @Override
            public String toString() {
                return classpathLocation + ".css()";
            }
        };
    }

    private void inlineStyleSheetImpl(InlineCtx ctx, String content,
            String styleLocation, String targetStyleLocation) {
        Map<String, Asset> processedAssets = ctx.processedAssets;

        // break endless loops
        if (!ctx.startedAssets.add(styleLocation)) {
            throw new RuntimeException(
                    "Import loop detected. Involved style sheets: "
                            + ctx.startedAssets);
        }

        // determine final name

        analyzer.process(content, ctx.sb, new CssProcessorHandler() {

            @Override
            public boolean shouldInline(String ref, String media) {
                return true;
            }

            @Override
            public String replaceRef(String ref) {
                Optional<Pair<String, String>> splitRef = normalizeRef(ref);

                if (!splitRef.isPresent())
                    return ref;

                String location = RiseUtil.resolvePath(styleLocation,
                        splitRef.get().getA());
                Asset asset = processedAssets.get(location);
                if (asset == null) {
                    asset = helper.loadAssetFromClasspath(location,
                            () -> "style sheet " + styleLocation);
                    asset = ctx.referencedAssetProcessor.apply(asset);
                    processedAssets.put(location, asset);
                    ctx.result.add(asset);
                }
                return RiseUtil.getShortestPath(targetStyleLocation,
                        asset.getName()) + splitRef.get().getB();
            }

            @Override
            public String replaceImportRef(String ref) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void performInline(StringBuffer sb, String ref,
                    String media) {
                String location = RiseUtil.resolvePath(styleLocation, ref);
                byte[] refData = RiseUtil.readFromClasspath(location,
                        getClass().getClassLoader());
                if (refData == null) {
                    throw new RuntimeException("Style sheet " + location
                            + " imported from " + styleLocation + " not found");
                }
                inlineStyleSheetImpl(ctx, new String(refData, Charsets.UTF_8),
                        location, targetStyleLocation);

            }
        });
        ctx.startedAssets.remove(styleLocation);

    }

    private String getTargetStyleLocation(String nameTemplate,
            String styleLocation) {
        String targetStyleLocation = helper
                .resolveNameTemplate(new Asset() {

                    @Override
                    public String getName() {
                        return styleLocation;
                    }

                    @Override
                    public byte[] getData() {
                        return new byte[] {};
                    }

                    @Override
                    public String getContentType() {
                        return config.getDefaultContentType(getAssetType());
                    }

                    @Override
                    public AssetType getAssetType() {
                        return DefaultAssetTypes.CSS;
                    }

                    @Override
                    public String getClasspathLocation() {
                        return null;
                    }
                }, nameTemplate);
        return targetStyleLocation;
    }

}
