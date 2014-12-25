package laf.core.translation.labels;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import laf.core.translation.labels.PropertyReflectionUtil.Property;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PropertyReflectionUtilTest {

	static class TestClassProperties {
		public int getR() {
			return 0;
		}

		public void setW(int arg) {
		}

		public int getRW() {
			return 0;
		}

		public void setRW(int arg) {
		}

		public int getA(int idx) {
			return 0;
		}

		public void setA(int idx, int arg) {
		}

		public static int getStatic() {
			return 0;
		}
	}

	class ClassA {
		public int getA() {
			return 0;
		}
	}

	interface InterfaceB {
		public int getB();

		default public int getB1() {
			return 1;
		}
	}

	class ClassC extends ClassA implements InterfaceB {

		@Override
		public int getA() {
			return 0;
		}

		@Override
		public int getB() {
			return 0;
		}
	}

	@InjectMocks
	PropertyReflectionUtil util;

	@Test
	public void testGetPropertiesA() throws Exception {
		Map<String, Property> props = util.getPropertyIntroductionMap(ClassA.class);
		assertEquals(1, props.size());
		assertEquals(new PropertyReflectionUtil.Property("a", ClassA.class,
				ClassA.class.getDeclaredMethod("getA"), null, null),
				props.get("a"));
	}

	@Test
	public void testGetPropertiesB() throws Exception {
		Map<String, Property> props = util.getPropertyIntroductionMap(InterfaceB.class);
		assertEquals(2, props.size());
		assertEquals(new PropertyReflectionUtil.Property("b", InterfaceB.class,
				InterfaceB.class.getDeclaredMethod("getB"), null, null),
				props.get("b"));
		assertEquals(new PropertyReflectionUtil.Property("b1",
				InterfaceB.class, InterfaceB.class.getDeclaredMethod("getB1"),
				null, null), props.get("b1"));
	}

	@Test
	public void testGetPropertiesC() throws Exception {
		Map<String, Property> props = util.getPropertyIntroductionMap(ClassC.class);
		assertEquals(3, props.size());
		assertEquals(new PropertyReflectionUtil.Property("a", ClassA.class,
				ClassA.class.getDeclaredMethod("getA"), null, null),
				props.get("a"));
		assertEquals(new PropertyReflectionUtil.Property("b", InterfaceB.class,
				InterfaceB.class.getDeclaredMethod("getB"), null, null),
				props.get("b"));
		assertEquals(new PropertyReflectionUtil.Property("b1",
				InterfaceB.class, InterfaceB.class.getDeclaredMethod("getB1"),
				null, null), props.get("b1"));
	}

	@Test
	public void testGetDeclaredProperties() throws Exception {
		Map<String, Property> props = util.getDeclaredProperties(ClassC.class);
		assertEquals(2, props.size());
		assertEquals(new PropertyReflectionUtil.Property("a", ClassC.class,
				ClassC.class.getDeclaredMethod("getA"), null, null),
				props.get("a"));
		assertEquals(new PropertyReflectionUtil.Property("b", ClassC.class,
				ClassC.class.getDeclaredMethod("getB"), null, null),
				props.get("b"));

	}

	@Test
	public void testGetDeclaredProperties1() throws Exception {
		Map<String, Property> props = util
				.getDeclaredProperties(TestClassProperties.class);
		assertEquals(4, props.size());
		assertEquals(
				new PropertyReflectionUtil.Property("r",
						TestClassProperties.class,
						TestClassProperties.class.getDeclaredMethod("getR"),
						null, null), props.get("r"));
		assertEquals(
				new PropertyReflectionUtil.Property("w",
						TestClassProperties.class, null,
						TestClassProperties.class.getDeclaredMethod("setW",
								int.class), null), props.get("w"));
		assertEquals(
				new PropertyReflectionUtil.Property("rW",
						TestClassProperties.class,
						TestClassProperties.class.getDeclaredMethod("getRW"),
						TestClassProperties.class.getDeclaredMethod("setRW",
								int.class), null), props.get("rW"));
		assertEquals(
				new PropertyReflectionUtil.Property("a",
						TestClassProperties.class,
						TestClassProperties.class.getDeclaredMethod("getA",
								int.class),
						TestClassProperties.class.getDeclaredMethod("setA",
								int.class, int.class), null), props.get("a"));
	}

}
