package com.github.ruediste.rise.core.web.assetPipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.web.assetPipeline.CssAnalyzer.CssProcessorHandler;
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
     * @param styleSheets
     *            the style sheets to relocate
     * @param namePattern
     *            pattern to use as style sheet name (see
     *            {@link AssetGroup#name(String)})
     * @param refencedAssetProcessor
     *            processor for the referenced assets. The assets will be
     *            included in the returned assets. The same classpath location
     *            will only be processed once.
     */
    public List<Asset> relocateStyleSheets(List<Asset> styleSheets,
            String namePattern, Function<Asset, Asset> refencedAssetProcessor) {

        Ctx ctx = new Ctx();
        ctx.referencedAssetProcessor = refencedAssetProcessor;
        ctx.nameTemplate = namePattern;

        for (Asset style : styleSheets) {
            String content = new String(style.getData(), Charsets.UTF_8);
            String styleLocation = style.getName();
            relocateStyleSheetImpl(ctx, content, styleLocation);
        }
        return ctx.result;
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
                String location = RiseUtil.resolvePath(styleLocation, ref);
                Asset asset = processedAssets.get(location);
                if (asset == null) {
                    asset = helper.loadAssetFromClasspath(location,
                            () -> "style sheet " + styleLocation);
                    asset = ctx.referencedAssetProcessor.apply(asset);
                    processedAssets.put(location, asset);
                }
                return RiseUtil.getShortestPath(targetStyleLocation,
                        asset.getName());
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
        Asset result = createCssAsset(targetStyleLocation, data);
        processedAssets.put(styleLocation, result);
        ctx.result.add(result);
        return result;
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
    public List<Asset> inlineStyleSheets(List<Asset> styleSheets,
            String nameTemplate,
            Function<Asset, Asset> refencedAssetProcessor) {

        InlineCtx ctx = new InlineCtx();
        ctx.referencedAssetProcessor = refencedAssetProcessor;
        ctx.nameTemplate = nameTemplate;

        for (Asset style : styleSheets) {
            ctx.sb = new StringBuffer();
            String content = new String(style.getData(), Charsets.UTF_8);
            String styleLocation = style.getName();
            String targetStyleLocation = getTargetStyleLocation(
                    ctx.nameTemplate, styleLocation);
            inlineStyleSheetImpl(ctx, content, styleLocation,
                    targetStyleLocation);

            byte[] data = ctx.sb.toString().getBytes(Charsets.UTF_8);
            Asset result = createCssAsset(targetStyleLocation, data);
            ctx.processedAssets.put(styleLocation, result);
            ctx.result.add(result);
        }
        return ctx.result;
    }

    private Asset createCssAsset(String name, byte[] data) {
        return new Asset() {

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
                String location = RiseUtil.resolvePath(styleLocation, ref);
                Asset asset = processedAssets.get(location);
                if (asset == null) {
                    asset = helper.loadAssetFromClasspath(location,
                            () -> "style sheet " + styleLocation);
                    asset = ctx.referencedAssetProcessor.apply(asset);
                    processedAssets.put(location, asset);
                }
                return RiseUtil.getShortestPath(targetStyleLocation,
                        asset.getName());
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
        String targetStyleLocation = helper.resolveNameTemplate(new Asset() {

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
        }, nameTemplate);
        return targetStyleLocation;
    }

}
