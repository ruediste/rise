package com.github.ruediste.laf.core.web.assetPipeline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AssetPathGroupTest {
	@Test
	public void testInsertMin() throws Exception {
		AssetPathGroup group = new AssetPathGroup(null);
		assertEquals("foo.min.bar", group.insertMin("foo.bar"));
		assertEquals("bar", group.insertMin("bar"));
	}
}
