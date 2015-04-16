package com.github.ruediste.laf.mvc.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.objectweb.asm.Type;

import com.github.ruediste.laf.test.SaltaTestBase;

public class MvcWebControllerReflectionUtilTest extends SaltaTestBase {

	@Inject
	MvcWebControllerReflectionUtil util;

	private class A {
	}

	private class B implements IEmbeddedControllerMvcWeb {
	}

	@Test
	public void testIsEmbeddedControllerType() throws Exception {
		assertTrue(util.isEmbeddedController(Type.getType(B.class)));
		assertFalse(util.isEmbeddedController(Type.getType(A.class)));
	}

}
