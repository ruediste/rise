package com.github.ruediste.rise.core.web.assetPipeline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AssetBundleTest {

    private final class Bundle extends AssetBundle {
    }

    @Test
    public void testCalculateFullPath() throws Exception {
        AssetBundle bundle = new Bundle();
        bundle.pipelineConfiguration = new AssetPipelineConfiguration();

        assertEquals("foo.css", bundle.calculateFullPath("/foo.css"));
        assertEquals(getClass().getPackage().getName().replace('.', '/')
                + "/foo.css", bundle.calculateFullPath("./foo.css"));
        assertEquals(Bundle.class.getName().replace('.', '/') + "-foo.css",
                bundle.calculateFullPath(".-foo.css"));
        assertEquals("assets/foo.css", bundle.calculateFullPath("foo.css"));
    }

}
