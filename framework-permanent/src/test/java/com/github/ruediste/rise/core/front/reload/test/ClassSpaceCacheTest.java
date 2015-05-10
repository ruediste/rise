package com.github.ruediste.rise.core.front.reload.test;

import static org.junit.Assert.assertTrue;

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
import com.github.ruediste.rise.core.front.reload.ClassSpaceCacheTestHelper;
import com.github.ruediste.rise.core.front.reload.Reloadable;
import com.github.ruediste.rise.core.front.reload.ReloadebleClassesIndex;

@RunWith(MockitoJUnitRunner.class)
@Reloadable
public class ClassSpaceCacheTest {
	@Mock
	Logger log;

	@InjectMocks
	ReloadebleClassesIndex cache;

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
		assertTrue(cache.isReloadable(A.class.getName()));
	}

	@Test
	public void testAnonymousClass() {
		assertTrue(cache.isReloadable(tst.getClass().getName()));
	}
}
