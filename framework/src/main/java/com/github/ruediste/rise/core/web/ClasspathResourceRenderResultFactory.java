package com.github.ruediste.rise.core.web;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetPipelineConfiguration;
import com.google.common.io.ByteStreams;

public class ClasspathResourceRenderResultFactory {
    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    AssetPipelineConfiguration pipelineConfig;

    @Inject
    CoreRequestInfo info;

    @Inject
    Logger log;

    @Inject
    ClassLoader classLoader;

    public HttpRenderResult create(String classpath) {
        // access resource
        InputStream in = classLoader.getResourceAsStream(classpath);

        if (in == null) {
            throw new RuntimeException("Asset " + classpath + " not found");
        }

        // set content type
        String contentType = null;
        {
            int idx = classpath.lastIndexOf('.');
            if (idx >= 0) {
                String tmp = pipelineConfig.getContentType(classpath.substring(idx + 1));
                if (tmp != null)
                    contentType = tmp;
            }
        }

        try {
            return new ContentRenderResult(ByteStreams.toByteArray(in), contentType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                log.warn("error while closing input");
            }
        }
    }

}
