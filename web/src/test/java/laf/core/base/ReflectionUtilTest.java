package laf.core.base;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;

public class ReflectionUtilTest {

	private class Base {

		@Inject
		public Number base(Number n) {
			return null;
		}
	}

	private class Derived extends Base {

		@Override
		public Integer base(Number n) {
			return null;
		}
	}

	private class Derived2 extends Derived {

	}

	@Test
	public void testIsAnnotationPresent() throws Exception {
		assertTrue(ReflectionUtil.isAnnotationPresent(Derived2.class,
				Derived.class.getMethod("base", Number.class), Inject.class));
	}

}
