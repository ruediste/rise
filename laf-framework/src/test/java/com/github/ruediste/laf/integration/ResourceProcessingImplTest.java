package com.github.ruediste.laf.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ResourceProcessingImplTest {

	private ResourceProcessing processing;

	@Before
	public void before() {
		processing = ResourceProcessingUtil.getProcessing();

	}

	@Test
	public void testMinifyCss() throws Exception {

		assertEquals(".foo{color:white;}\r\n", ResourceProcessingUtil.process(
				".foo {\n  color: white;\n}", processing::minifyCss));
	}
}
