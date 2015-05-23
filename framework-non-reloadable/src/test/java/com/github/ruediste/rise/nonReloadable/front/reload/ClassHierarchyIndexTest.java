package com.github.ruediste.rise.nonReloadable.front.reload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier.ClassChangeTransaction;
import com.github.ruediste.rise.util.AsmUtil;

@RunWith(MockitoJUnitRunner.class)
public class ClassHierarchyIndexTest {

    @Mock
    Logger log;

    @InjectMocks
    ClassHierarchyIndex cache;

    @Before
    public void before() throws IOException {
        ClassChangeTransaction trx = new ClassChangeNotifier.ClassChangeTransaction();
        trx.addedClasses.add(AsmUtil.readClass(IA.class));
        trx.addedClasses.add(AsmUtil.readClass(A.class));
        trx.addedClasses.add(AsmUtil.readClass(IB.class));
        trx.addedClasses.add(AsmUtil.readClass(B.class));
        trx.addedClasses.add(AsmUtil.readClass(Base.class));
        trx.addedClasses.add(AsmUtil.readClass(Derived1.class));
        trx.addedClasses.add(AsmUtil.readClass(Derived2.class));
        cache.onChange(trx);
    }

    private interface IA {
    }

    private interface IB extends IA {
    }

    private class A implements IA {
    }

    private class B extends A implements IB {
    }

    private class Base<T> {
    }

    private class Derived1<P, T> extends Base<T> {
    }

    private class Derived2 extends Derived1<Integer, String> {
    }

    @Test
    public void testIsAssignableFrom() throws Exception {
        assertTrue(isSubtype(A.class, IA.class));
        assertFalse(isSubtype(A.class, IB.class));
        assertTrue(isSubtype(A.class, A.class));
        assertFalse(isSubtype(A.class, B.class));

        assertTrue(isSubtype(B.class, IA.class));
        assertTrue(isSubtype(B.class, IB.class));
        assertTrue(isSubtype(B.class, A.class));
        assertTrue(isSubtype(B.class, B.class));

        assertTrue(isSubtype(IA.class, IA.class));
        assertFalse(isSubtype(IA.class, IB.class));
        assertFalse(isSubtype(IA.class, A.class));
        assertFalse(isSubtype(IA.class, B.class));

        assertTrue(isSubtype(IB.class, IA.class));
        assertTrue(isSubtype(IB.class, IB.class));
        assertFalse(isSubtype(IB.class, A.class));
        assertFalse(isSubtype(IB.class, B.class));

    }

    protected boolean isSubtype(Class<?> sub, Class<?> parent) {
        return cache.isAssignableFrom(Type.getType(parent).getInternalName(),
                Type.getType(sub).getInternalName());
    }

    @Test
    public void testResolve() {
        assertEquals(
                "java/lang/String",
                cache.resolve(Type.getInternalName(Derived2.class),
                        Type.getInternalName(Base.class), "T"));
        assertNull(cache.resolve(Type.getInternalName(Derived1.class),
                Type.getInternalName(Base.class), "T"));
    }
}
