package com.github.ruediste.rise.core.web.assetPipeline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AssetLocationGroupTest {
    @Test
    public void testInsertMin() throws Exception {
        AssetLocationGroup group = new AssetLocationGroup(null);
        assertEquals("foo.min.bar", group.insertMin("foo.bar"));
        assertEquals("bar", group.insertMin("bar"));
    }
}
