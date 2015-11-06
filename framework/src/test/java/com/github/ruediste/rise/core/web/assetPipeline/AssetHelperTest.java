package com.github.ruediste.rise.core.web.assetPipeline;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

@RunWith(MockitoJUnitRunner.class)
public class AssetHelperTest {

    Asset resource;

    String resourceHash;

    AssetGroup group;

    @Mock
    AssetPipelineConfiguration config;

    @InjectMocks
    AssetHelper helper;

    @InjectMocks
    AssetBundle bundle = new AssetBundle() {
        @Override
        protected void initialize() {
        }

    };

    @Before
    public void setup() throws UnsupportedEncodingException {
        resource = new TestAsset("test.js", "Hello");
        resourceHash = BaseEncoding
                .base64Url().encode(Hashing.sha256()
                        .hashBytes(resource.getData()).asBytes())
                .substring(0, 3);
        group = new AssetGroup(bundle, (List<Asset>) null);
        when(config.getExtension(DefaultAssetTypes.CSS)).thenReturn("css");
        when(config.getAssetBasePath()).thenReturn("assets/");
        when(config.getDefaultHashLength()).thenReturn(3);
    }

    @Test
    public void testResolveNameTemplate() throws Exception {
        assertEquals("foo.js", helper.resolveNameTemplate(resource, "foo.js"));
        assertEquals(resourceHash + ".js",
                helper.resolveNameTemplate(resource, "{hash}.js"));
        assertEquals("test/" + resourceHash + ".js",
                helper.resolveNameTemplate(resource, "test/{hash}.js"));
        assertEquals("foo/test.css",
                helper.resolveNameTemplate(resource, "foo/{name}.css"));
        resource = new TestAsset("foo.bar.sass", "Hello");
        assertEquals("foo/foo.bar.css",
                helper.resolveNameTemplate(resource, "foo/{name}.css"));
        assertEquals("hell{o}",
                helper.resolveNameTemplate(resource, "hell\\{o}"));
        assertEquals("hell\\o",
                helper.resolveNameTemplate(resource, "hell\\\\o"));

    }

    @Test
    public void testResolveNameTemplateQualifiedName() {
        resource = new TestAsset("foo/bar.css", "Hello");
        assertEquals("static/foo/bar.js",
                helper.resolveNameTemplate(resource, "static/{qname}.js"));

    }

    @Test
    public void testResolveNameTemplateExt() {
        resource = new TestAsset("foo/bar.css", "Hello");
        assertEquals("yeah.css",
                helper.resolveNameTemplate(resource, "yeah.{ext}"));

    }

    @Test
    public void testResolveNameTemplateTypeExt() {
        resource = new TestAsset("foo/bar.js", "Hello");
        assertEquals("yeah.css",
                helper.resolveNameTemplate(resource, "yeah.{extT}"));

    }

    @Test
    public void testResolveNameTemplateHash() {
        resource = new TestAsset("foo/bar.js", "Hello");
        assertEquals("GF-.css",
                helper.resolveNameTemplate(resource, "{hash}.{extT}"));
        assertEquals("barGF-.css",
                helper.resolveNameTemplate(resource, "{name}{hash}.{extT}"));

    }

    @Test
    public void testCalculateAbsoluteLocation() throws Exception {

        assertEquals("foo.css",
                helper.calculateAbsoluteLocation("/foo.css", getClass()));
        assertEquals(
                getClass().getPackage().getName().replace('.', '/')
                        + "/foo.css",
                bundle.calculateAbsoluteLocation("./foo.css"));
        assertEquals(getClass().getName().replace('.', '/') + "-foo.css",
                helper.calculateAbsoluteLocation(".-foo.css", getClass()));
        assertEquals("assets/foo.css",
                helper.calculateAbsoluteLocation("foo.css", getClass()));
    }
}
