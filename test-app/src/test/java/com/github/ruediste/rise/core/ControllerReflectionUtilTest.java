package com.github.ruediste.rise.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.testApp.WebTest;

public class ControllerReflectionUtilTest extends WebTest {

    @Inject
    ControllerReflectionUtil util;

    @Inject
    ClassHierarchyIndex cache;

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
