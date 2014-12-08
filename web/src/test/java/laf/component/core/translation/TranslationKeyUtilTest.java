package laf.component.core.translation;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

public class TranslationKeyUtilTest {

	private static class TestClassBase {
		public int baseField;

		public void method() {

		}

		public void baseMethod() {

		}
	}

	private static class TestClassDerived extends TestClassBase {
		public int derivedField;

		@Override
		public void method() {
		}

		public void derivedMethod() {

		}
	}

	@Test
	public void testField() throws NoSuchFieldException, SecurityException {
		TestClassDerived
		PropertyUtils.getPropertyDescriptor(getClass(), null)
		assertEquals(TestClassBase.class.getName() + ".baseField",
				TranslationKeyUtil.getKey(TestClassBase.class,
						TestClassBase.class.getField("baseField")));
	}
}
