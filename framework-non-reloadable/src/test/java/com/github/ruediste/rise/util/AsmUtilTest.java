package com.github.ruediste.rise.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.Test;
import org.objectweb.asm.Type;

public class AsmUtilTest {

	private abstract class A {
		abstract int m();

		abstract void m(String s);

		abstract void m(int i);

		abstract void m(int i1, short i2);

		abstract void m(String... strs);

		abstract void m(int... is);

		abstract void m(@SuppressWarnings("unchecked") List<Object>... os);
	}

	ClassLoader classLoader = AsmUtilTest.class.getClassLoader();

	@Test
	public void testLoadClassTypeClassLoader() throws Exception {
		assertEquals(A.class,
				AsmUtil.loadClass(Type.getType(A.class), classLoader));
	}

	@Test
	public void testLoadMethod() throws Exception {
		check("()I");
		check("(Ljava/lang/String;)V", String.class);
		check("(I)V", int.class);
		check("(IS)V", int.class, short.class);
		check("([Ljava/lang/String;)V", String[].class);
		check("([I)V", int[].class);
		check("([Ljava/util/List;)V", List[].class);
	}

	private void check(String desc, Class<?>... paramTypes) throws Exception {
		assertEquals(A.class.getDeclaredMethod("m", paramTypes),
				AsmUtil.loadMethod(Type.getInternalName(A.class), "m", desc,
						classLoader));
	}

	void i(Integer a) {
	}

	@Test
	public void testGetOverrideDesc() throws Exception {
		assertEquals(AsmUtil.getOverrideDesc("foo", "()I"),
				AsmUtil.getOverrideDesc("foo", "()J"));
		assertNotEquals(AsmUtil.getOverrideDesc("foo", "()I"),
				AsmUtil.getOverrideDesc("bar", "()I"));
		assertNotEquals(
				AsmUtil.getOverrideDesc("foo", "(Ljava/lang/Integer;)V"),
				AsmUtil.getOverrideDesc("foo", "(Ljava/lang/Boolean;)V"));
	}
}
