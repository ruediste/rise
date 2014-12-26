package laf.core.translation.labels;

import static org.junit.Assert.*;

import java.lang.annotation.*;

import laf.core.translation.TString;

import org.junit.Before;
import org.junit.Test;

public class LabelUtilTest {

	LabelUtil util;

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
	@Documented
	@LabelVariant("short")
	public @interface Short {
		String value();
	}

	@Labeled
	@Label("Ruedi")
	public class Nina {

	}

	@PropertiesLabeled(variants = { "foo", "short" })
	@Labeled(variants = { "foo1" })
	@Label(value = "myFoo", variant = "foo")
	@Short("typeShort")
	private static class TestClass {
		@SuppressWarnings("unused")
		public void setFooBar(int x) {
		}

		@Label("myLabel")
		@Label(value = "myLabelFoo", variant = "foo")
		@Short("propertyShort")
		public void getLabeled(int x) {
		}

		@SuppressWarnings("unused")
		public void get漢字() {

		}
	}

	private static class TestClassUnlabeled {
		@SuppressWarnings("unused")
		public void setFooBar(int x) {
		}
	}

	private static class TestClassDerived extends TestClass {

	}

	@MembersLabeled(variants = { "foo", "short" })
	private enum TestEnum {
		MEMBER_A, @Label("myB")
		@Label(value = "myFoo", variant = "foo")
		@Short("memberShort")
		MEMBER_B
	}

	private enum TestEnumUnlabeled {
		MEMBER_A
	}

	@Before
	public void setup() {
		util = new LabelUtil();
		util.reflectionUtil = new PropertyReflectionUtil();
	}

	@Test
	public void testGetPropertyLabelDirect() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestClass.fooBar",
				"Foo Bar"), util.getPropertyLabel(TestClass.class, "fooBar"));
	}

	@Test
	public void testGetPropertyLabelLabelAnnotation() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestClass.labeled",
				"myLabel"), util.getPropertyLabel(TestClass.class, "labeled"));
	}

	@Test
	public void testGetPropertyLabelVariant() throws Exception {
		assertEquals(
				new TString(
						"laf.core.translation.labels.LabelUtilTest$TestClass.labeled.foo",
						"myLabelFoo"), util.getPropertyLabel(TestClass.class,
						"labeled", "foo"));
	}

	@Test
	public void testGetPropertyLabelVariantShort() throws Exception {
		assertEquals(
				new TString(
						"laf.core.translation.labels.LabelUtilTest$TestClass.labeled.short",
						"propertyShort"), util.getPropertyLabel(
						TestClass.class, "labeled", "short"));
	}

	@Test(expected = RuntimeException.class)
	public void testGetPropertyLabelInexistantVariant() throws Exception {
		util.getPropertyLabel(TestClass.class, "labeled", "bar");
	}

	@Test(expected = RuntimeException.class)
	public void testGetPropertyLabelUnlabeled() throws Exception {
		assertNull(util.getPropertyLabel(TestClassUnlabeled.class, "fooBar"));
	}

	@Test
	public void testGetPropertyLabelUnicode() throws Exception {
		assertEquals(
				new TString(
						"laf.core.translation.labels.LabelUtilTest$TestClass.漢字",
						"漢字"), util.getPropertyLabel(TestClass.class, "漢字"));
	}

	@Test
	public void testGetPropertyLabelDerived() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestClass.fooBar",
				"Foo Bar"), util.getPropertyLabel(TestClassDerived.class,
				"fooBar"));
	}

	@Test
	public void testGetTypeLabel() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestClass",
				"Test Class"), util.getTypeLabel(TestClass.class));
	}

	@Test(expected = RuntimeException.class)
	public void testGetTypeLabelUnlabeled() throws Exception {
		util.getTypeLabel(TestClassUnlabeled.class);
	}

	@Test(expected = RuntimeException.class)
	public void testGetTypeLabelMissingVariant() throws Exception {
		util.getTypeLabel(TestClass.class, "bar");
	}

	@Test
	public void testGetTypeLabelVariant() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestClass.foo",
				"myFoo"), util.getTypeLabel(TestClass.class, "foo"));
	}

	@Test
	public void testGetTypeLabelVariantShort() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestClass.short",
				"typeShort"), util.getTypeLabel(TestClass.class, "short"));
	}

	@Test
	public void testGetTypeLabelVariantNoLabel() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestClass.foo1",
				"Test Class"), util.getTypeLabel(TestClass.class, "foo1"));
	}

	@Test
	public void testGetEnumMemberLabel() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestEnum.MEMBER_A",
				"Member A"), util.getEnumMemberLabel(TestEnum.MEMBER_A));
	}

	@Test
	public void testGetEnumMemberLabelLabelAnnotation() throws Exception {
		assertEquals(new TString(
				"laf.core.translation.labels.LabelUtilTest$TestEnum.MEMBER_B",
				"myB"), util.getEnumMemberLabel(TestEnum.MEMBER_B));
	}

	@Test(expected = RuntimeException.class)
	public void testGetEnumMemberLabelUnlabeled() throws Exception {
		util.getEnumMemberLabel(TestEnumUnlabeled.MEMBER_A);
	}

	@Test(expected = RuntimeException.class)
	public void testGetEnumMemberLabelInexistantVariant() throws Exception {
		util.getEnumMemberLabel(TestEnum.MEMBER_A, "bar");
	}

	@Test
	public void testGetEnumMemberLabelVariant() throws Exception {
		assertEquals(
				new TString(
						"laf.core.translation.labels.LabelUtilTest$TestEnum.MEMBER_B.foo",
						"myFoo"), util.getEnumMemberLabel(TestEnum.MEMBER_B,
						"foo"));
	}

	@Test
	public void testGetEnumMemberLabelVariantShort() throws Exception {
		assertEquals(
				new TString(
						"laf.core.translation.labels.LabelUtilTest$TestEnum.MEMBER_B.short",
						"memberShort"), util.getEnumMemberLabel(
						TestEnum.MEMBER_B, "short"));
	}
}
