package laf.core.translation.labels;

import static org.junit.Assert.assertEquals;
import laf.core.translation.TString;

import org.junit.Before;
import org.junit.Test;

public class LabelUtilTest {

	LabelUtil util;

	private static class TestClass {
		@SuppressWarnings("unused")
		public void setFooBar(int x) {
		}
	}

	private static class TestClassDerived extends TestClass {

	}

	@Before
	public void setup() {
		util = new LabelUtil();
		util.reflectionUtil = new PropertyReflectionUtil();
	}

	@Test
	public void testGetLabelDirect() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestClass.fooBar",
				"Foo Bar"), util.getLabel(TestClass.class, "fooBar"));
	}

	@Test
	public void testGetLabelDerived() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestClass.fooBar",
				"Foo Bar"), util.getLabel(TestClassDerived.class, "fooBar"));
	}
}
