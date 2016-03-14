package com.github.ruediste.rise.core.web.assetPipeline;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.util.RiseUtil;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

public class AssetHelper {
    @Inject
    AssetPipelineConfiguration pipelineConfiguration;

    @Inject
    CoreUtil coreUtil;

    /**
     * Generate the URL for an asset. This can be used to replace urls enclosed
     * for example in CSS files
     */
    public String url(Asset asset) {
        return coreUtil.urlStatic(getPathInfo(asset));
    }

    private String getExtension(String path) {
        return Iterables.getLast(Splitter.on('.').split(path));
    }

    /**
     * Map the path of an asset to the full resouce path to be used to load the
     * asset from the classpath. Rules see
     * {@link AssetBundle#locations(String...)}
     */
    public String calculateAbsoluteLocation(String location, Class<?> bundleClass) {
        return calculateAbsoluteLocation(location, pipelineConfiguration.getAssetBasePath(), bundleClass);
    }

    public Asset loadAssetFromClasspath(String path, Class<?> bundleClass) {
        return loadAssetFromClasspath(path, () -> bundleClass.getName());
    }

    /**
     * @param fullPath
     *            path to load the asset from
     * @param referenceTextSupplier
     *            supplier for the text shown in error messages if the asset
     *            could not be loaded
     */
    public Asset loadAssetFromClasspath(String fullPath, Supplier<String> referenceTextSupplier) {
        AssetType type = pipelineConfiguration.getDefaultAssetType(getExtension(fullPath));
        return new Asset() {

            @Override
            public byte[] getData() {
                byte[] result = RiseUtil.readFromClasspath(fullPath, getClass().getClassLoader());
                if (result == null)
                    throw new RuntimeException("Unable to find resource on classpath: " + fullPath + ", reference from "
                            + referenceTextSupplier.get());
                return result;
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

            @Override
            public String getClasspathLocation() {
                return fullPath;
            }

            @Override
            public String getName() {
                String basePath = pipelineConfiguration.getAssetBasePath();
                if (!Strings.isNullOrEmpty(fullPath) && fullPath.startsWith(basePath))
                    return fullPath.substring(basePath.length());
                return fullPath;
            }
        };
    }

    static String getPackageName(Class<?> cls) {
        String classname = cls.getName();
        int index = classname.lastIndexOf('.');
        if (index != -1)
            return classname.substring(0, index);
        return "";
    }

    /**
     * Map the path of an asset to the full resouce path to be used to load the
     * asset from the classpath. Rules see
     * {@link AssetBundle#locations(String...)}
     */
    public static String calculateAbsoluteLocation(String location, String basePath, Class<?> cls) {
        if (location.startsWith("/"))
            return location.substring(1);
        if (location.startsWith("./"))
            return AssetHelper.getPackageName(cls).replace('.', '/') + location.substring(1);
        if (location.startsWith("."))
            return cls.getName().replace('.', '/') + location.substring(1);
        return basePath + location;
    }

    /**
     * resolve a name template
     * 
     * @see AssetGroup#name(String)
     */
    public String resolveNameTemplate(Asset asset, String template) {
        String name = asset.getName();
        Pattern p = Pattern.compile("(?<context>\\A|[^\\\\]|\\G)\\{(?<placeholder>[^\\}]*)\\}");
        Matcher m = p.matcher(template);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;
        while (m.find()) {
            sb.append(template.substring(lastEnd, m.end("context")));

            lastEnd = m.end();
            String placeholder = m.group("placeholder");
            switch (placeholder) {
            case "hash": {
                String hash = BaseEncoding.base64Url().encode(Hashing.sha256().hashBytes(asset.getData()).asBytes());
                sb.append(hash.substring(0, Integer.min(hash.length(), pipelineConfiguration.getDefaultHashLength())));
            }
                break;
            case "name": {
                String[] parts = name.split("/");
                parts = parts[parts.length - 1].split("\\.");

                sb.append(Arrays.asList(parts).subList(0, parts.length == 1 ? 1 : parts.length - 1).stream()
                        .collect(Collectors.joining(".")));
            }
                break;
            case "qname": {
                String[] parts = name.split("\\.");

                sb.append(Arrays.asList(parts).subList(0, parts.length == 1 ? 1 : parts.length - 1).stream()
                        .collect(Collectors.joining(".")));
            }
                break;
            case "ext": {
                String[] parts = name.split("\\.");
                sb.append(parts[parts.length - 1]);
            }
                break;
            case "extT": {
                sb.append(pipelineConfiguration.getExtension(asset.getAssetType()));
            }
                break;
            default:
                throw new RuntimeException("Unknown placeholder " + placeholder + " in name template " + template);
            }
        }
        sb.append(template.substring(lastEnd, template.length()));
        return sb.toString().replace("\\{", "{").replace("\\\\", "\\");
    }

    public PathInfo getPathInfo(Asset asset) {
        return new PathInfo(getAssetPathInfo(asset.getName()));
    }

    public String getAssetPathInfo(String name) {
        if (name.startsWith("/"))
            return name;
        else
            return pipelineConfiguration.assetPathInfoPrefix + name;
    }
}
