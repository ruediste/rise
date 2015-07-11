package com.github.ruediste.rise.core.web.assetDir;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.rise.core.web.assetPipeline.AssetPipelineConfiguration;
import com.github.ruediste.rise.nonReloadable.front.StartupTimeLogger;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.DirectoryChangeWatcher;
import com.github.ruediste.rise.util.AsmUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;

public class AssetDirRequestMapper {
    @Inject
    Logger log;

    private final static class AssetDirRequestParseResult implements
            RequestParseResult {
        @Inject
        CoreConfiguration coreConfiguration;

        @Inject
        AssetPipelineConfiguration pipelineConfig;

        @Inject
        CoreRequestInfo info;

        @Inject
        Logger log;

        private String absoluteLocation;

        private String pathInfoPrefix;

        public AssetDirRequestParseResult initialize(String absoluteLocation,
                String pathInfoPrefix) {
            this.absoluteLocation = absoluteLocation;
            this.pathInfoPrefix = pathInfoPrefix;
            return this;
        }

        @Override
        public void handle() {
            // determine location
            HttpRequest request = info.getRequest();
            String fullLocation = absoluteLocation
                    + request.getPathInfo().substring(pathInfoPrefix.length());

            if (fullLocation.endsWith("/")) {
                throw new RuntimeException("Not serving directory listings");
            }
            // access resource
            InputStream in = coreConfiguration.dynamicClassLoader
                    .getResourceAsStream(fullLocation);

            if (in == null) {
                throw new RuntimeException("Asset " + fullLocation
                        + " not found");
            }

            HttpServletResponse response = info.getServletResponse();
            response.setStatus(HttpServletResponse.SC_OK);

            // set content type
            {
                int idx = fullLocation.lastIndexOf('.');
                if (idx >= 0) {
                    String contentType = pipelineConfig
                            .getContentType(fullLocation.substring(idx + 1));
                    if (contentType != null)
                        response.setContentType(contentType);
                }
            }

            try (OutputStream out = response.getOutputStream()) {
                // read data fully. This will be needed anyways for ETAG
                // support (based on a checksum)
                byte[] data = ByteStreams.toByteArray(in);
                response.setContentLength(data.length);
                out.write(data, 0, data.length);
            } catch (IOException e) {
                log.warn("Error while sending asset response", e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("error while closing input");
                }
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
    javax.inject.Provider<AssetDirRequestParseResult> resultProvider;

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
            String absoluteLocation = AssetBundle.calculateAbsoluteLocation(
                    location, pipelineConfiguration.assetBasePath,
                    dir.getClass());

            String name = dir.getName();
            if (name == null)
                name = absoluteLocation;

            String pathInfoPrefix = pipelineConfiguration.assetPathInfoPrefix
                    + name;

            dir.pathInfoPrefix = pathInfoPrefix;

            index.registerPrefix(pathInfoPrefix, request -> resultProvider
                    .get().initialize(absoluteLocation, pathInfoPrefix));
            log.debug("Registered asset directory {} ({} -> {})", dir
                    .getClass().getSimpleName(), absoluteLocation,
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
        dirClass = AsmUtil.loadClass(Type.getObjectType(cls.name),
                coreConfiguration.dynamicClassLoader);

        AssetDir dir = (AssetDir) injector.getInstance(dirClass);
        dirs.add(dir);
    }
}
