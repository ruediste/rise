package com.github.ruediste.laf.core.classReload;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;

import com.github.ruediste.laf.core.classReload.dynamicTest.TestDynamicSpaceClassPackage;
import com.github.ruediste.laf.core.classReload.dynamicTest.TestPermanentSpacePackage;
import com.github.ruediste.laf.test.SaltaTest;

public class ClassSpaceCacheTest extends SaltaTest {

	@Inject
	ClassSpaceCache cache;

	@Test
	public void testNotAnnotated() {
		assertEquals(PermanentSpace.class,
				cache.getClassSpace(ClassSpaceCacheTest.class.getName()));

	}

	@Test
	public void testDynamic() {
		assertEquals(DynamicSpace.class,
				cache.getClassSpace(TestDynamicSpaceClass.class.getName()));

	}

	@Test
	public void testDynamicPackage() {
		assertEquals(DynamicSpace.class,
				cache.getClassSpace(TestDynamicSpaceClassPackage.class
						.getName()));

	}

	@Test
	public void testPermanentPackage() {
		assertEquals(PermanentSpace.class,
				cache.getClassSpace(TestPermanentSpacePackage.class.getName()));

	}

	@Test
	public void testDynamicInner() {
		assertEquals(
				DynamicSpace.class,
				cache.getClassSpace(TestDynamicSpaceClass.Inner.class.getName()));
		assertEquals(DynamicSpace.class,
				cache.getClassSpace(TestDynamicSpaceClass.Inner2.class
						.getName()));

	}
}
