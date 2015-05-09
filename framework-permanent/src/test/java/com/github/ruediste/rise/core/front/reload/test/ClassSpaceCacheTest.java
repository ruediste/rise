package com.github.ruediste.rise.core.front.reload.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.github.ruediste.rise.core.front.reload.ClassChangeNotifier;
import com.github.ruediste.rise.core.front.reload.ClassChangeNotifier.ClassChangeTransaction;
import com.github.ruediste.rise.core.front.reload.ClassHierarchyCacheTest;
import com.github.ruediste.rise.core.front.reload.ClassSpaceCache;
import com.github.ruediste.rise.core.front.reload.ClassSpaceCacheTestHelper;
import com.github.ruediste.rise.core.front.reload.DynamicSpace;

@RunWith(MockitoJUnitRunner.class)
@DynamicSpace
public class ClassSpaceCacheTest {
	@Mock
	Logger log;

	@InjectMocks
	ClassSpaceCache cache;

	private Object tst;

	private class A {
	}

	@Before
	public void before() throws IOException {
		tst = new Object() {
		};
		ClassChangeTransaction trx = new ClassChangeNotifier.ClassChangeTransaction();
		trx.addedClasses.add(ClassHierarchyCacheTest
				.readClass(ClassSpaceCacheTest.class));
		trx.addedClasses.add(ClassHierarchyCacheTest.readClass(A.class));
		trx.addedClasses.add(ClassHierarchyCacheTest.readClass(tst.getClass()));
		ClassSpaceCacheTestHelper.callOnChange(cache, trx);
	}

	@Test
	public void testInnerClass() {
		assertEquals(DynamicSpace.class, cache.getClassSpace(A.class.getName()));
	}

	@Test
	public void testAnonymousClass() {
		assertEquals(DynamicSpace.class,
				cache.getClassSpace(tst.getClass().getName()));
	}
}
