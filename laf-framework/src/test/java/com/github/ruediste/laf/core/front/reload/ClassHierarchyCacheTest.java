package com.github.ruediste.laf.core.front.reload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import net.sf.cglib.asm.Type;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.front.reload.ClassChangeNotifier.ClassChangeTransaction;

public class ClassHierarchyCacheTest {

	ClassHierarchyCache cache;

	@Before
	public void before() throws IOException {
		cache = new ClassHierarchyCache();
		ClassChangeTransaction trx = new ClassChangeNotifier.ClassChangeTransaction();
		trx.addedClasses.add(readClass(IA.class));
		trx.addedClasses.add(readClass(A.class));
		trx.addedClasses.add(readClass(IB.class));
		trx.addedClasses.add(readClass(B.class));
		cache.onChange(trx);
	}

	private ClassNode readClass(Class<?> cls) throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				cls.getName().replace('.', '/') + ".class");
		ClassNode result = new ClassNode();
		new ClassReader(in).accept(result, 0);
		in.close();
		return result;
	}

	private interface IA {
	}

	private interface IB extends IA {
	}

	private class A implements IA {
	}

	private class B extends A implements IB {
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

}
