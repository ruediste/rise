package com.github.ruediste.laf.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ResourceProcessingImplTest {

	private ResourceProcessing processing;

	@Before
	public void before() {
		processing = IntegrationClassLoader.loadAndInstantiate(
				ResourceProcessing.class, ResourceProcessingImpl.class);

	}

	@Test
	public void testMinifyCss() throws Exception {

		assertEquals(".foo{color:white;}\r\n", ResourceProcessing.process(
				".foo {\n  color: white;\n}", processing::minifyCss));
	}
}