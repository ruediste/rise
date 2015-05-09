package com.github.ruediste.rise.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.ControllerReflectionUtil;
import com.github.ruediste.rise.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.rise.test.SaltaTestBase;

public class ControllerReflectionUtilTest extends SaltaTestBase {

	@Inject
	ControllerReflectionUtil util;

	@Inject
	ClassHierarchyCache cache;

	private class A {
		public ActionResult m1() {
			return null;
		}

		public String m2() {
			return null;
		}
	}

	@Test
	public void testIsActionMethodMethodNode() throws Exception {
		MethodNode m1 = cache.getNode(A.class).methods.get(1);
		assertEquals("m1", m1.name);
		assertTrue(util.isActionMethod(m1));
		MethodNode m2 = cache.getNode(A.class).methods.get(2);
		assertEquals("m2", m2.name);
		assertFalse(util.isActionMethod(m2));
	}

}
