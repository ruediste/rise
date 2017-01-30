package com.github.ruediste.rise.core.web.assetDir;

import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.web.ClasspathResourceRenderResultFactory;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.HttpRenderResultUtil;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.integration.AssetBundle;
import com.github.ruediste.rise.nonReloadable.front.reload.ClasspathResourceIndex;
import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

@Singleton
public class AssetDirRequestMapper {
    @Inject
    Logger log;

    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    PathInfoIndex index;

    @Inject
    CoreRequestInfo info;

    @Inject
    ClasspathResourceRenderResultFactory factory;

    @Inject
    HttpRenderResultUtil httpRenderResultUtil;

    @Inject
    ClasspathResourceIndex resourceIndex;

    @Inject
    CoreUtil util;

    @Inject
    ClassLoader classLoader;

    private class Bundle {
        public Deque<String> cssUrls = new ArrayDeque<>();
        public Deque<String> jsUrls = new ArrayDeque<>();
    }

    private Map<String, Bundle> bundles = new HashMap<>();

    public Iterable<String> getJsUrls(AssetBundle bundle) {
        Bundle b = bundles.get(bundle.toString());
        if (b == null) {
            return Collections.emptyList();
        }
        return b.jsUrls;
    }

    public Iterable<String> getCssUrls(AssetBundle bundle) {
        Bundle b = bundles.get(bundle.toString());
        if (b == null) {
            return Collections.emptyList();
        }
        return b.cssUrls;
    }

    private Bundle getBundle(String lowerCamelName) {
        String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, lowerCamelName);
        Bundle result = bundles.get(name);
        if (result == null) {
            result = new Bundle();
            bundles.put(name, result);
        }
        return result;
    }

    private class AssetEntry {
        String name;
        String bundle;
        int nr;
        String extension;

        public AssetEntry(String name, String bundle, int nr, String extension) {
            super();
            this.name = name;
            this.bundle = bundle;
            this.nr = nr;
            this.extension = extension;
        }

    }

    private static Pattern devPattern = Pattern.compile("(?<bundle>.*)-segment-(?<nr>\\d+)\\.(?<ext>[a-z]*)");

    public void initialize() {
        ArrayList<AssetEntry> assetEntries = new ArrayList<>();
        for (String resource : resourceIndex.getResourcesByGlob("assets/**/*")) {
            if (!resource.startsWith("assets/"))
                continue;
            String name = resource.substring("assets/".length());

            // register resource
            AssetRequestParseResult result = new AssetRequestParseResult("assets/" + name);
            {
                String pathInfo;
                if (name.startsWith("root/"))
                    pathInfo = name.substring("root".length());
                else
                    pathInfo = coreConfiguration.assetsPrefix + name;
                index.registerPathInfo(pathInfo, request -> result);
            }

            if (name.contains("/"))
                continue;
            if (coreConfiguration.isAssetsProdMode()) {
                if (handleProdResource(name, ".css.list", (bundle, url) -> bundle.cssUrls.add(url)))
                    continue;
                if (handleProdResource(name, ".js.list", (bundle, url) -> bundle.jsUrls.add(url)))
                    continue;
            } else {
                Matcher matcher = devPattern.matcher(name);
                if (matcher.matches()) {
                    assetEntries.add(new AssetEntry(name, matcher.group("bundle"),
                            Integer.parseInt(matcher.group("nr")), matcher.group("ext")));
                    continue;
                }
            }

        }
        if (!coreConfiguration.isAssetsProdMode()) {
            for (Entry<String, List<AssetEntry>> entry : assetEntries.stream().collect(groupingBy(x -> x.bundle))
                    .entrySet()) {
                Bundle bundle = getBundle(entry.getKey());

                entry.getValue().stream().sorted(Comparator.comparing(x -> x.nr)).forEach(e -> {
                    String pathInfo = coreConfiguration.assetsPrefix + e.name;
                    String url = util.urlStatic(new PathInfo(pathInfo));
                    if ("css".equals(e.extension))
                        bundle.cssUrls.add(url);
                    else if ("js".equals(e.extension))
                        bundle.jsUrls.add(url);
                });
                ;
            }
        }
    }

    private boolean handleProdResource(String name, String suffix, BiConsumer<Bundle, String> urlHandler) {
        if (name.endsWith(suffix)) {
            Bundle bundle = getBundle(name.substring(0, name.length() - suffix.length()));
            try {
                JSONArray array = new JSONArray(readFromClasspath("assets/" + name));
                for (int i = 0; i < array.length(); i++) {
                    String fileName = array.getString(i);
                    String pathInfo = "/assets/" + fileName;
                    urlHandler.accept(bundle, util.urlStatic(new PathInfo(pathInfo)));
                }
                return true;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private String readFromClasspath(String name) {
        try (InputStream in = classLoader.getResourceAsStream(name)) {
            return new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean handleDevResource(String name, String suffix, BiConsumer<Bundle, String> urlHandler) {
        if (name.endsWith(suffix)) {
            Bundle bundle = getBundle(name.substring(0, name.length() - suffix.length()));
            String pathInfo = coreConfiguration.assetsPrefix + name;
            urlHandler.accept(bundle, util.urlStatic(new PathInfo(pathInfo)));
            return true;
        }
        return false;
    }

    private final class AssetRequestParseResult implements RequestParseResult {

        private HttpRenderResult result;

        public AssetRequestParseResult(String classpath) {
            result = factory.create(classpath);
        }

        @Override
        public void handle() {
            try {
                result.sendTo(info.getServletResponse(), httpRenderResultUtil);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
