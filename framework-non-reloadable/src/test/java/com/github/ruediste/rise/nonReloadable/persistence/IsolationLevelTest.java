package com.github.ruediste.rise.nonReloadable.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IsolationLevelTest {

	@Test
	public void testIsLowerThan() {
		assertTrue(IsolationLevel.REPEATABLE_READ.isLowerThan(IsolationLevel.SERIALIZABLE));
		assertFalse(IsolationLevel.SERIALIZABLE.isLowerThan(IsolationLevel.REPEATABLE_READ));
		assertFalse(IsolationLevel.SERIALIZABLE.isLowerThan(IsolationLevel.SERIALIZABLE));
		assertFalse(IsolationLevel.REPEATABLE_READ.isLowerThan(IsolationLevel.REPEATABLE_READ));
	}
}
