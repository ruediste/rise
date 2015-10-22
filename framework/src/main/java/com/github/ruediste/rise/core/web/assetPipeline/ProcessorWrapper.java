package com.github.ruediste.rise.core.web.assetPipeline;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.base.Charsets;

import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;

public class ProcessorWrapper implements Function<Asset, Asset> {
    @Inject
    Provider<AssetPipelineConfiguration> config;

    ResourcePostProcessor processor;

    private Supplier<ResourcePostProcessor> processorSupplier;

    private AssetType targetAssetType;

    public ProcessorWrapper initialize(AssetType targetAssetType,
            ResourcePostProcessor processor) {
        return initialize(targetAssetType, () -> processor);
    }

    public ProcessorWrapper initialize(AssetType targetAssetType,
            Supplier<ResourcePostProcessor> processorSupplier) {
        this.targetAssetType = targetAssetType;
        this.processorSupplier = processorSupplier;
        return this;
    }

    private ResourcePostProcessor getProcessor() {
        if (processor == null) {
            synchronized (this) {
                if (processor == null)
                    processor = processorSupplier.get();
            }
        }
        return processor;
    }

    @Override
    public Asset apply(Asset t) {
        String input = new String(t.getData(), Charsets.UTF_8);
        StringWriter output = new StringWriter();
        try {
            getProcessor().process(new StringReader(input), output);
        } catch (IOException e) {
            throw new RuntimeException("Error while processing asset " + t, e);
        }
        return new Asset() {

            @Override
            public String getClasspathLocation() {
                return t.getClasspathLocation();
            }

            @Override
            public String getName() {
                return t.getName();
            }

            @Override
            public AssetType getAssetType() {
                return targetAssetType;
            }

            @Override
            public String getContentType() {
                return config.get().getDefaultContentType(getAssetType());
            }

            @Override
            public byte[] getData() {
                return output.getBuffer().toString().getBytes(Charsets.UTF_8);
            }

            @Override
            public String toString() {
                return t + "." + getProcessor().getClass().getSimpleName()
                        + "()";
            }

        };
    }

}
