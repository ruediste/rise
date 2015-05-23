package com.github.ruediste.rise.core.web.assetPipeline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.ruediste.rise.core.web.assetPipeline.AssetPathGroup;

public class AssetPathGroupTest {
    @Test
    public void testInsertMin() throws Exception {
        AssetPathGroup group = new AssetPathGroup(null);
        assertEquals("foo.min.bar", group.insertMin("foo.bar"));
        assertEquals("bar", group.insertMin("bar"));
    }
}
