package com.github.ruediste.laf.core.classReload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.ruediste.laf.core.classReload.DynamicClassLoader.DynamicSource;


public class DynamicClassLoaderTest {

	@Test
	public void testExclusion(){
		DynamicSource src=new DynamicSource();
		assertFalse(src.isExcluded("foo"));
		
		src.addExclusion("*foo");
		assertTrue(src.isExcluded("foo"));
		assertTrue(src.isExcluded("Hellofoo"));
		assertFalse(src.isExcluded("fooBar"));
		
		src.exclusionPatterns.clear();
		src.addExclusion("foo.*.bar");
		assertFalse(src.isExcluded("foo"));
		assertTrue(src.isExcluded("foo.bla.bar"));
		assertFalse(src.isExcluded("foo.bla.bla.bar"));
		
		src.exclusionPatterns.clear();
		src.addExclusion("foo.**.bar");
		assertFalse(src.isExcluded("foo"));
		assertTrue(src.isExcluded("foo.bla.bar"));
		assertTrue(src.isExcluded("foo.bla.bla.bar"));
	}
}
