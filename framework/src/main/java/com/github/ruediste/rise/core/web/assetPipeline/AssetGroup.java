package com.github.ruediste.rise.core.web.assetPipeline;

import static java.util.stream.Collectors.joining;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.rise.util.Pair;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;

/**
 * Manages a group of {@link Asset}s
 */
public class AssetGroup {
    public final List<Asset> assets;
    public final AssetBundle bundle;

    public AssetGroup(AssetBundle bundle, List<Asset> assets) {
        this.bundle = bundle;
        this.assets = assets;
    }

    public AssetGroup(AssetBundle bundle, Stream<Asset> resources) {
        this(bundle, resources.collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return "AssetGroup" + assets;
    }

    public AssetGroup map(Function<Asset, Asset> processor) {
        return new AssetGroup(bundle, assets.stream().map(processor)
                .collect(Collectors.toList()));
    }

    public void send(Consumer<Asset> consumer) {
        assets.forEach(consumer);
    }

    @SafeVarargs
    final public AssetGroup fork(final Consumer<AssetGroup>... consumers) {
        for (Consumer<AssetGroup> consumer : consumers) {
            consumer.accept(this);
        }

        return this;
    }

    /**
     * If the current {@link AssetMode} matches the given asset mode, let all
     * assets pass. Otherwise returns an empty group
     */
    public AssetGroup filter(AssetMode mode) {
        if (bundle.getAssetMode() == mode) {
            return this;
        } else {
            return new AssetGroup(bundle, Collections.emptyList());
        }

    }

    /**
     * Return the single asset of this group. Raises an error if not exactly one
     * asset is in the group
     */
    public Asset single() {
        if (assets.size() == 0)
            throw new RuntimeException("Asset group is emtpy");
        if (assets.size() > 1)
            throw new RuntimeException(
                    "Asset group contains more than a single asset: "
                            + assets.stream().map(Asset::getName)
                                    .collect(joining(", ")));
        return assets.get(0);
    }

    /**
     * In {@link AssetMode#PRODUCTION Production Mode}, let all assets pass.
     * Otherwise returns an empty group
     */
    public AssetGroup prod() {
        return filter(AssetMode.PRODUCTION);
    }

    /**
     * In {@link AssetMode#DEVELOPMENT Development Mode}, let all assets pass.
     * Otherwise returns an empty group
     */
    public AssetGroup dev() {
        return filter(AssetMode.DEVELOPMENT);
    }

    /**
     * Evaluate each branch with this group and join the resulting groups
     */
    @SafeVarargs
    final public AssetGroup forkJoin(
            Function<AssetGroup, AssetGroup>... branches) {
        ArrayList<Asset> list = new ArrayList<>();
        for (Function<AssetGroup, AssetGroup> branch : branches) {
            list.addAll(branch.apply(this).assets);
        }
        return new AssetGroup(bundle, list);
    }

    /**
     * Add the assets of the other group to this group
     */
    public AssetGroup join(AssetGroup... others) {
        ArrayList<Asset> list = new ArrayList<>();
        list.addAll(assets);
        for (AssetGroup other : others)
            list.addAll(other.assets);
        return new AssetGroup(bundle, list);
    }

    /**
     * Set the names of the {@link Asset}s in this group based on a template.
     * The following placeholders are supported:
     * <ul>
     * <li><b>hash:</b> hash code of the underlying data
     * <li><b>name:</b> name of the underlying asset, without extension or path
     * <li><b>qname:</b> name of the underlying asset, includint the path, but
     * without extension
     * <li><b>ext:</b> extenstion from the name of the underlying asset
     * <li><b>extT:</b> extension from the {@link AssetType} of the underlying
     * asset
     * </ul>
     */
    public AssetGroup name(String template) {
        Pattern p = Pattern.compile("(\\A|[^\\\\])\\{hash\\}");
        Matcher m = p.matcher(template);
        boolean usesHash = m.find();

        // delegate to a caching Asset to avoid retrieving the
        // data multiple times if hashing is used
        AssetGroup underlying = usesHash ? cache() : this;

        AssetGroup result = underlying.map(asset -> new DelegatingAsset(asset) {

            @Override
            public String getName() {
                return resolveNameTemplate(asset, template);
            }

            @Override
            public String toString() {
                return asset + ".name(" + template + ")";
            };
        });

        // cache again to avoid calculating the name multiple time
        // when hashing is used
        return usesHash ? result.cache() : result;
    }

    String resolveNameTemplate(Asset asset, String template) {
        String name = asset.getName();
        Pattern p = Pattern
                .compile("(\\A|[^\\\\])\\{(?<placeholder>[^\\}]*)\\}");
        Matcher m = p.matcher(template);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;
        while (m.find()) {
            sb.append(template.substring(lastEnd,
                    m.start() == 0 ? 0 : m.start() + 1));
            lastEnd = m.end();
            String placeholder = m.group("placeholder");
            switch (placeholder) {
            case "hash":
                sb.append(Hashing.sha256().hashBytes(asset.getData())
                        .toString());
                break;
            case "name": {
                String[] parts = name.split("/");
                parts = parts[parts.length - 1].split("\\.");

                sb.append(Arrays.asList(parts)
                        .subList(0, parts.length == 1 ? 1 : parts.length - 1)
                        .stream().collect(Collectors.joining(".")));
            }
                break;
            case "qname": {
                String[] parts = name.split("\\.");

                sb.append(Arrays.asList(parts)
                        .subList(0, parts.length == 1 ? 1 : parts.length - 1)
                        .stream().collect(Collectors.joining(".")));
            }
                break;
            case "ext": {
                String[] parts = name.split("\\.");
                sb.append(parts[parts.length - 1]);
            }
                break;
            case "extT": {
                sb.append(bundle.pipelineConfiguration.getExtension(asset
                        .getAssetType()));
            }
                break;
            default:
                throw new RuntimeException("Unknown placeholder " + placeholder
                        + " in name template " + template);
            }
        }
        sb.append(template.substring(lastEnd, template.length()));
        return sb.toString().replace("\\{", "{").replace("\\\\", "\\");
    }

    public AssetGroup filterName(Predicate<String> predicate) {
        Predicate<? super Asset> tmp = r -> predicate.test(r.getName());
        return filter(tmp);
    }

    /**
     * keep only the assets that match the given predicate
     */
    public AssetGroup filter(Predicate<? super Asset> predicate) {
        return new AssetGroup(bundle, assets.stream().filter(predicate));
    }

    /**
     * Filter by file extension
     *
     * @param extension
     *            extension to filter for, without leading period. Example: "js"
     */
    public AssetGroup filterExtension(String extension) {
        return filterName(name -> name.endsWith("." + extension));
    }

    /**
     * Asset group where the data in the assets get's cached.
     * 
     * @see AssetGroup#eager()
     */
    public AssetGroup cache() {
        return new AssetGroup(bundle, assets.stream().<Asset> map(
                r -> new CachingAsset(r, bundle)));
    }

    /**
     * Asset caching the results of a delegate
     */
    private static class CachingAsset implements Asset {
        static AttachedProperty<AttachedPropertyBearer, String> name = new AttachedProperty<>(
                "name");
        static AttachedProperty<AttachedPropertyBearer, String> contentType = new AttachedProperty<>(
                "contentType");
        static AttachedProperty<AttachedPropertyBearer, AssetType> assetType = new AttachedProperty<>(
                "assetType");
        static AttachedProperty<AttachedPropertyBearer, byte[]> data = new AttachedProperty<>(
                "byte[]");

        private Asset delegate;

        private AttachedPropertyBearer cache;

        public CachingAsset(Asset delegate, AssetBundle bundle) {
            Preconditions.checkNotNull(delegate);
            Preconditions.checkNotNull(bundle);
            this.delegate = delegate;
            cache = bundle.cache;
        }

        @Override
        public String getName() {
            return name.setIfAbsent(cache, delegate::getName);
        }

        @Override
        public AssetType getAssetType() {
            return assetType.setIfAbsent(cache, delegate::getAssetType);
        }

        @Override
        public String getContentType() {
            return contentType.setIfAbsent(cache, delegate::getContentType);
        }

        @Override
        public byte[] getData() {
            return data.setIfAbsent(cache, delegate::getData);
        }

        @Override
        public String toString() {
            return delegate + ".cache()";
        }
    }

    /**
     * Combine all {@link Asset}s in the group into one asset for each
     * combination of {@link Asset#getAssetType()} and
     * {@link Asset#getContentType()}.
     * 
     * @param nameTemplate
     *            name template for the generated assets. The underlying name is
     *            set to the empty string. see {@link #name} for details on the
     *            template format
     */
    public AssetGroup combine(String nameTemplate) {
        Multimap<Pair<AssetType, String>, Asset> map = ArrayListMultimap
                .create();
        for (Asset asset : assets) {
            map.put(Pair.of(asset.getAssetType(), asset.getContentType()),
                    asset);
        }
        return new AssetGroup(bundle, map
                .asMap()
                .entrySet()
                .stream()
                .map(entry -> new CombineAsset(entry.getValue(), entry.getKey()
                        .getA(), entry.getKey().getB())));

    }

    /**
     * Asset merging multiple assets into a single one
     */
    private final static class CombineAsset implements Asset {
        private Collection<Asset> assets;
        private AssetType assetType;
        private String contentType;

        public CombineAsset(Collection<Asset> assets, AssetType assetType,
                String contentType) {
            this.assets = assets;
            this.assetType = assetType;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public byte[] getData() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                for (Asset res : assets) {
                    baos.write(res.getData());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return baos.toByteArray();
        }

        @Override
        public AssetType getAssetType() {
            return assetType;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String toString() {
            return "combine" + assets;
        }
    }

    public void forEach(Consumer<? super Asset> action) {
        assets.stream().forEach(action);
    }

    public AssetGroup replace(String target, String replacement) {
        return replace(target, replacement, Charsets.UTF_8);
    }

    public AssetGroup replace(String target, String replacement, Charset charset) {
        return mapData(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s.replace(target, replacement);
            }

            @Override
            public String toString() {
                return "replace(" + target + "," + replacement + ")";
            }
        }, charset);
    }

    public AssetGroup mapData(Function<String, String> func) {
        return mapData(func, Charsets.UTF_8);
    }

    public AssetGroup mapData(Function<String, String> func, Charset charset) {
        return map(asset -> {
            return new DelegatingAsset(asset) {
                @Override
                public byte[] getData() {
                    return func.apply(new String(asset.getData(), charset))
                            .getBytes(charset);
                }

                @Override
                public String toString() {
                    return asset + "mapData(" + func + "," + charset + ")";
                }
            };
        }).cache();
    }

}
