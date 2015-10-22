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
        resourceHash = Hashing.sha256().hashBytes(resource.getData())
                .toString();
        group = new AssetGroup(bundle, (List<Asset>) null);
        when(config.getExtension(DefaultAssetTypes.CSS)).thenReturn("css");
    }

    @Test
        public void testResolvePathInfoTemplate() throws Exception {
            assertEquals("foo.js", helper.resolvePathInfoTemplate(resource, "foo.js"));
            assertEquals(resourceHash + ".js",
                    helper.resolvePathInfoTemplate(resource, "{hash}.js"));
            assertEquals("test/" + resourceHash + ".js",
                    helper.resolvePathInfoTemplate(resource, "test/{hash}.js"));
            assertEquals("foo/test.css",
                    helper.resolvePathInfoTemplate(resource, "foo/{name}.css"));
            resource = new TestAsset("foo.bar.sass", "Hello");
            assertEquals("foo/foo.bar.css",
                    helper.resolvePathInfoTemplate(resource, "foo/{name}.css"));
            assertEquals("hell{o}",
                    helper.resolvePathInfoTemplate(resource, "hell\\{o}"));
            assertEquals("hell\\o",
                    helper.resolvePathInfoTemplate(resource, "hell\\\\o"));
    
        }

    @Test
        public void testResolvePathInfoTemplateQualifiedName() {
            resource = new TestAsset("foo/bar.css", "Hello");
            assertEquals("static/foo/bar.js",
                    helper.resolvePathInfoTemplate(resource, "static/{qname}.js"));
    
        }

    @Test
        public void testResolvePathInfoTemplateExt() {
            resource = new TestAsset("foo/bar.css", "Hello");
            assertEquals("yeah.css",
                    helper.resolvePathInfoTemplate(resource, "yeah.{ext}"));
    
        }

    @Test
        public void testResolvePathInfoTemplateTypeExt() {
            resource = new TestAsset("foo/bar.js", "Hello");
            assertEquals("yeah.css",
                    helper.resolvePathInfoTemplate(resource, "yeah.{extT}"));
    
        }

    @Test
        public void testResolvePathInfoTemplateHash() {
            resource = new TestAsset("foo/bar.js", "Hello");
            assertEquals(
                    "185f8db32271fe25f561a6fc938b2e264306ec304eda518007d1764826381969.css",
                    helper.resolvePathInfoTemplate(resource, "{hash}.{extT}"));
            assertEquals(
                    "bar185f8db32271fe25f561a6fc938b2e264306ec304eda518007d1764826381969.css",
                    helper.resolvePathInfoTemplate(resource, "{name}{hash}.{extT}"));
    
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
