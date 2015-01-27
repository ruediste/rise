package com.github.ruediste.laf.component.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.laf.component.web.CWRenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class CWRenderUtilTest {

	@InjectMocks
	CWRenderUtil util;

	@Test
	public void testCombineClasses() throws Exception {
		assertEquals("hello", util.combineClasses("hello"));
		assertEquals("foo bar", util.combineClasses("foo", "bar"));
		assertEquals("foo bar", util.combineClasses("foo", " bar"));
		assertEquals("foo bar foo2", util.combineClasses("foo bar", " foo2"));
		assertEquals("foo", util.combineClasses("foo", null));
	}

}
