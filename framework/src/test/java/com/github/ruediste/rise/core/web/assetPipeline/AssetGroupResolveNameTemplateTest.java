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

@RunWith(MockitoJUnitRunner.class)
public class AssetGroupResolveNameTemplateTest {

    Asset resource;

    String resourceHash;

    AssetGroup group;

    @Mock
    AssetPipelineConfiguration config;

    @InjectMocks
    AssetBundle bundle = new AssetBundle() {
        @Override
        protected void initialize() {
        }

    };

    @Before
    public void setup() throws UnsupportedEncodingException {
        resource = new TestAsset("test.js", "Hello");
        resourceHash = Hashing.sha256().hashBytes(resource.getData())
                .toString();
        group = new AssetGroup(bundle, (List<Asset>) null);
    }

    @Test
    public void testResolveNameTemplate() throws Exception {
        assertEquals("foo.js", group.bundle.helper.resolveNameTemplate(resource, "foo.js"));
        assertEquals(resourceHash + ".js",
                group.bundle.helper.resolveNameTemplate(resource, "{hash}.js"));
        assertEquals("test/" + resourceHash + ".js",
                group.bundle.helper.resolveNameTemplate(resource, "test/{hash}.js"));
        assertEquals("foo/test.css",
                group.bundle.helper.resolveNameTemplate(resource, "foo/{name}.css"));
        resource = new TestAsset("foo.bar.sass", "Hello");
        assertEquals("foo/foo.bar.css",
                group.bundle.helper.resolveNameTemplate(resource, "foo/{name}.css"));
        assertEquals("hell{o}",
                group.bundle.helper.resolveNameTemplate(resource, "hell\\{o}"));
        assertEquals("hell\\o",
                group.bundle.helper.resolveNameTemplate(resource, "hell\\\\o"));

    }

    @Test
    public void testResolveNameTemplateQualifiedName() {
        resource = new TestAsset("foo/bar.css", "Hello");
        assertEquals("static/foo/bar.js",
                group.bundle.helper.resolveNameTemplate(resource, "static/{qname}.js"));

    }

    @Test
    public void testResolveNameTemplateExt() {
        resource = new TestAsset("foo/bar.css", "Hello");
        assertEquals("yeah.css",
                group.bundle.helper.resolveNameTemplate(resource, "yeah.{ext}"));

    }

    @Test
    public void testResolveNameTemplateTypeExt() {
        resource = new TestAsset("foo/bar.js", "Hello");
        when(config.getExtension(DefaultAssetTypes.CSS)).thenReturn("css");
        assertEquals("yeah.css",
                group.bundle.helper.resolveNameTemplate(resource, "yeah.{extT}"));

    }

    @Test
    public void testResolveNameTemplateHash() {
        resource = new TestAsset("foo/bar.js", "Hello");
        when(config.getExtension(DefaultAssetTypes.CSS)).thenReturn("css");
        assertEquals(
                "185f8db32271fe25f561a6fc938b2e264306ec304eda518007d1764826381969.css",
                group.bundle.helper.resolveNameTemplate(resource, "{hash}.{extT}"));

    }
}
