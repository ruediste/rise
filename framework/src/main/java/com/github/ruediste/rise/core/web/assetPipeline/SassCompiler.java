package com.github.ruediste.rise.core.web.assetPipeline;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import javax.inject.Inject;

import com.github.ruediste.rise.util.RiseUtil;
import com.google.common.base.Charsets;

import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import io.bit3.jsass.OutputStyle;
import io.bit3.jsass.context.StringContext;
import io.bit3.jsass.importer.Import;
import io.bit3.jsass.importer.Importer;

public class SassCompiler {

    @Inject
    AssetPipelineConfiguration config;

    @Inject
    AssetHelper helper;

    private class ClasspathImporter implements Importer {

        @Override
        public Collection<Import> apply(String url, Import previous) {
            Import result = previous;
            URI base = previous.getBase();
            if ("classpath".equals(base.getScheme())) {

                String path = RiseUtil.resolvePath(base.getPath(), url).substring(1);
                String data = RiseUtil.readFromClasspathAsString(path, getClass().getClassLoader());
                if (data != null)
                    result = new Import(toUri(path), toUri(path), data);
            }
            return Arrays.asList(result);
        }

    }

    public Function<Asset, Asset> create(String targetNamePattern) {
        return sass -> {
            String targetPathInfo = helper.getAssetPathInfo(getTargetName(targetNamePattern, sass));
            Compiler compiler = new Compiler();
            Options options = new Options();
            options.setImporters(Arrays.asList(new ClasspathImporter()));
            options.setIsIndentedSyntaxSrc(false);
            options.setOutputStyle(OutputStyle.EXPANDED);
            try {
                String classpathLocation = sass.getClasspathLocation();
                StringContext ctx = new StringContext(sass.getDataString(), toUri(classpathLocation),
                        new URI(targetPathInfo), options);
                Output output = compiler.compile(ctx);
                if (output.getErrorStatus() != 0)
                    throw new RuntimeException(output.getErrorMessage());
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
                        return classpathLocation;
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
        };
    }

    private String getTargetName(String targetNamePattern, Asset sass) {
        return helper.resolveNameTemplate(new Asset() {

            @Override
            public String getName() {
                return sass.getName();
            }

            @Override
            public byte[] getData() {
                return new byte[] {};
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
        }, targetNamePattern);
    }

    private URI toUri(String classpathLocation) {
        try {
            return new URI("classpath:/" + classpathLocation);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error while creating URI from " + classpathLocation, e);
        }
    }

}
