package com.github.ruediste.rise.core.web.assetDir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.web.ClasspathResourceRenderResultFactory;
import com.github.ruediste.rise.core.web.HttpRenderResultUtil;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetHelper;
import com.github.ruediste.rise.core.web.assetPipeline.AssetPipelineConfiguration;
import com.github.ruediste.rise.nonReloadable.front.CurrentRestartableApplicationHolder;
import com.github.ruediste.rise.nonReloadable.front.StartupTimeLogger;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.util.AsmUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Stopwatch;

public class AssetDirRequestMapper {
    @Inject
    Logger log;

    private final static class AssetDirRequestParseResult implements RequestParseResult {

        @Inject
        CoreRequestInfo info;

        @Inject
        ClasspathResourceRenderResultFactory factory;

        @Inject
        HttpRenderResultUtil httpRenderResultUtil;

        private String absoluteLocation;

        private String pathInfoPrefix;

        public AssetDirRequestParseResult initialize(String absoluteLocation, String pathInfoPrefix) {
            this.absoluteLocation = absoluteLocation;
            this.pathInfoPrefix = pathInfoPrefix;
            return this;
        }

        @Override
        public void handle() {
            // determine location
            HttpRequest request = info.getRequest();
            String classpath = absoluteLocation + request.getPathInfo().substring(pathInfoPrefix.length());

            if (classpath.endsWith("/")) {
                throw new RuntimeException("Not serving directory listings");
            }

            try {
                factory.create(classpath).sendTo(info.getServletResponse(), httpRenderResultUtil);
            } catch (IOException e) {
                throw new RuntimeException(e);
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
    javax.inject.Provider<AssetDirRequestParseResult> resultProvider;

    @Inject
    CurrentRestartableApplicationHolder appHolder;

    @Inject
    ClassLoader classLoader;

    private List<AssetDir> dirs = new ArrayList<>();

    public void initialize() {
        Stopwatch watch = Stopwatch.createStarted();
        String internalName = Type.getInternalName(AssetDir.class);
        registerChilDirs(internalName);
        registerDirs(dirs);
        StartupTimeLogger.stopAndLog("Asset Directory Registration", watch);
    }

    void registerDirs(List<AssetDir> dirs) {

        for (AssetDir dir : dirs) {
            String location = dir.getLocation();
            String absoluteLocation = AssetHelper.calculateAbsoluteLocation(location,
                    pipelineConfiguration.getAssetBasePath(), dir.getClass());

            String pathInfoPrefix = dir.getName();
            if (pathInfoPrefix == null)
                pathInfoPrefix = absoluteLocation;
            else {
                if (!pathInfoPrefix.startsWith("/"))
                    pathInfoPrefix = pipelineConfiguration.assetPathInfoPrefix + pathInfoPrefix;

            }

            String pathInfoPrefixFinal = pathInfoPrefix;

            dir.pathInfoPrefix = pathInfoPrefix;

            index.registerPrefix(pathInfoPrefix,
                    request -> resultProvider.get().initialize(absoluteLocation, pathInfoPrefixFinal));
            log.debug("Registered asset directory {} ({} -> {})", dir.getClass().getSimpleName(), absoluteLocation,
                    pathInfoPrefix);
        }
    }

    public String getPathInfoString(AssetDir dir, String subPath) {
        return dir.pathInfoPrefix + subPath;
    }

    public PathInfo getPathInfo(AssetDir dir, String subPath) {
        return new PathInfo(getPathInfoString(dir, subPath));
    }

    void registerChilDirs(String internalName) {
        for (ClassNode child : cache.getChildren(internalName)) {
            registerBundle(child);
            registerChilDirs(child.name);
        }
    }

    void registerBundle(ClassNode cls) {
        Class<?> dirClass;
        dirClass = AsmUtil.loadClass(Type.getObjectType(cls.name), classLoader);

        AssetDir dir = (AssetDir) injector.getInstance(dirClass);
        dirs.add(dir);
    }
}
