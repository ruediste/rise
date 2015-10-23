package com.github.ruediste.rise.core.web.assetPipeline;

import java.net.URI;
import java.util.function.Function;

import javax.inject.Inject;

import com.google.common.base.Charsets;

import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import io.bit3.jsass.context.StringContext;

public class SassCompiler implements Function<Asset, Asset> {

    @Inject
    AssetPipelineConfiguration config;

    @Override
    public Asset apply(Asset sass) {
        Compiler compiler = new Compiler();
        Options options = new Options();
        options.setIsIndentedSyntaxSrc(false);
        try {
            StringContext ctx = new StringContext("", new URI("classpath:/foo"),
                    new URI("classpath:/foo"), options);
            Output output = compiler.compile(ctx);
            return new Asset() {

                @Override
                public String getName() {
                    return sass.getName();
                }

                @Override
                public byte[] getData() {
                    return output.getCss().getBytes(Charsets.UTF_8);
                }

                @Override
                public String getContentType() {
                    return config.getDefaultContentType(DefaultAssetTypes.CSS);
                }

                @Override
                public String getClasspathLocation() {
                    return sass.getClasspathLocation();
                }

                @Override
                public AssetType getAssetType() {
                    return DefaultAssetTypes.CSS;
                }

                @Override
                public String toString() {
                    return sass + ".sass()";
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
