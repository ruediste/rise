package com.github.ruediste.rise.core.web.assetPipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.nonReloadable.front.StartupTimeLogger;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.DirectoryChangeWatcher;
import com.github.ruediste.rise.util.AsmUtil;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@Singleton
public class AssetRequestMapper {
    @Inject
    Logger log;

    private final static class AssetRequestParseResult
            implements RequestParseResult {
        @Inject
        CoreRequestInfo info;

        @Inject
        Logger log;

        private Asset asset;

        AssetRequestParseResult initialize(Asset asset) {
            this.asset = asset;
            return this;

        }

        @Override
        public void handle() {
            HttpServletResponse response = info.getServletResponse();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(asset.getContentType());
            byte[] data = asset.getData();
            response.setContentLength(data.length);
            try (OutputStream out = response.getOutputStream()) {
                out.write(data, 0, data.length);
            } catch (IOException e) {
                log.warn("Error while sending asset response", e);
            }
        }
    }

    @Inject
    ClassHierarchyIndex cache;

    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    AssetPipelineConfiguration pipelineConfiguration;

    @Inject
    Injector injector;

    @Inject
    PathInfoIndex index;

    @Inject
    DirectoryChangeWatcher watcher;

    @Inject
    javax.inject.Provider<AssetRequestParseResult> resultProvider;

    @Inject
    AssetHelper helper;

    private List<AssetBundle> bundles = new ArrayList<>();

    public void initialize() {
        Stopwatch watch = Stopwatch.createStarted();

        // collect all bundles
        String internalName = Type.getInternalName(AssetBundle.class);
        registerChildBundles(internalName);

        // call initialize methods in correct order
        queuedForInitialization.forEach(AssetBundle::initialize);

        // register bundles with PathInfoIndex
        registerAssets(bundles);

        StartupTimeLogger.stopAndLog("Asset Bundle Registration", watch);
    }

    void registerAssets(List<AssetBundle> bundles) {
        Multimap<String, Pair<AssetBundle, Asset>> assets = ArrayListMultimap
                .create();

        for (AssetBundle bundle : bundles) {
            for (AssetBundleOutput output : bundle.outputs) {
                for (Asset asset : output.getAssets()) {
                    String pathInfo = helper.getAssetPathInfo(asset.getName());
                    assets.put(pathInfo, Pair.of(bundle, asset));

                }
            }
        }

        TreeSet<String> logMessages = new TreeSet<>();

        for (Entry<String, Collection<Pair<AssetBundle, Asset>>> entry : assets
                .asMap().entrySet()) {

            byte[] data = null;
            Pair<AssetBundle, Asset> first = null;
            for (Pair<AssetBundle, Asset> pair : entry.getValue()) {
                byte[] tmp = pair.getB().getData();
                if (data == null) {
                    data = tmp;
                    first = pair;
                } else {
                    // there is more than one asset with the same URL. make
                    // sure it has the same data
                    if (!Arrays.equals(data, tmp)) {
                        throw new RuntimeException(
                                "Two Assets map to the same name "
                                        + entry.getKey()
                                        + " but contain different data. They are declared in the following bundles:\n"
                                        + pair.getA().getClass().getName()
                                        + "\n -> " + pair.getB() + "\n"
                                        + first.getA().getClass().getName()
                                        + "\n -> " + first.getB());
                    }
                }
            }

            // all assets mapping to this path have the same data. Just pick the
            // first
            Asset asset = first.getB();
            index.registerPathInfo(entry.getKey(),
                    x -> resultProvider.get().initialize(asset));
            if (log.isDebugEnabled())
                logMessages.add(String.format("Registered %s -> %s",
                        first.getA().getClass().getSimpleName(),
                        entry.getKey()));
        }
        if (log.isDebugEnabled())
            logMessages.forEach(x -> log.debug(x));
    }

    void registerChildBundles(String internalName) {
        for (ClassNode child : cache.getChildren(internalName)) {
            registerBundle(child);
            registerChildBundles(child.name);
        }
    }

    void registerBundle(ClassNode cls) {
        Class<?> bundleClass;
        bundleClass = AsmUtil.loadClass(Type.getObjectType(cls.name),
                coreConfiguration.dynamicClassLoader);

        AssetBundle bundle = (AssetBundle) injector.getInstance(bundleClass);
        bundles.add(bundle);
    }

    private ArrayList<AssetBundle> queuedForInitialization = new ArrayList<AssetBundle>();

    /**
     * Called from the initialize of {@link AssetBundle}. Used to call the
     * initialize() methods in the correct order.
     */
    public void queueInitialization(AssetBundle assetBundle) {
        queuedForInitialization.add(assetBundle);
    }
}
