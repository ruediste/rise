package laf.component.core.binding;

import static laf.component.core.binding.transformers.Transformers.dateToString;
import static org.junit.Assert.assertEquals;

import java.util.*;

import laf.core.base.attachedProperties.AttachedPropertyBearerBase;

import org.junit.Test;

public class BindingUtilTest {

	public static class TestA extends AttachedPropertyBearerBase {
		private int valueA;
		private int valueRO;
		private String valueString;

		@SuppressWarnings("unused")
		private int valueWO;
		private TestB b;

		public int getValueA() {
			return valueA;
		}

		public void setValueA(int valueA) {
			this.valueA = valueA;
		}

		public TestB getB() {
			return b;
		}

		public void setB(TestB b) {
			this.b = b;
		}

		public int getValueRO() {
			return valueRO;
		}

		public void setValueWO(int valueWO) {
			this.valueWO = valueWO;
		}

		public String getValueString() {
			return valueString;
		}

		public void setValueString(String valueString) {
			this.valueString = valueString;
		}

	}

	public static class TestB {
		private int valueB;
		private TestC c;

		private Date valueDate;

		public int getValueB() {
			return valueB;
		}

		public void setValueB(int valueB) {
			this.valueB = valueB;
		}

		public TestC getC() {
			return c;
		}

		public void setC(TestC c) {
			this.c = c;
		}

		public Date getValueDate() {
			return valueDate;
		}

		public void setValueDate(Date valueDate) {
			this.valueDate = valueDate;
		}
	}

	public static class TestC {
		private int valueC;

		public int getValueC() {
			return valueC;
		}

		public void setValueC(int valueC) {
			this.valueC = valueC;
		}
	}

	@Test
	public void simple() {
		TestA a = new TestA();
		TestB b = new TestB();

		BindingGroup<TestB> groupB = new BindingGroup<>(TestB.class);
		groupB.set(b);

		// establish binding
		BindingUtil.bind(a, x -> x.setValueA(groupB.proxy().getValueB()));

		// pull up
		b.valueB = 2;
		groupB.pullUp();
		assertEquals(2, a.valueA);

		// push down
		a.valueA = 3;
		groupB.pushDown();
		assertEquals(3, b.valueB);
	}

	@Test
	public void simpleTransformer() {
		TestA a = new TestA();
		TestB b = new TestB();

		BindingGroup<TestB> groupB = new BindingGroup<>(TestB.class);
		groupB.set(b);

		// establish binding
		BindingUtil.bind(a, x -> x.setValueString(dateToString(groupB.proxy()
				.getValueDate())));

		// pull up
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.clear();
		cal.set(2014, 0, 1);
		b.valueDate = cal.getTime();
		groupB.pullUp();
		assertEquals("1/1/14", a.valueString);

		// push down
		a.valueString = "1/2/14";
		groupB.pushDown();
		cal.set(2014, 0, 2);
		assertEquals(cal.getTime(), b.valueDate);
	}

	@Test
	public void simpleOneWay() {
		TestA a = new TestA();
		TestB b = new TestB();

		BindingGroup<TestB> groupB = new BindingGroup<>(TestB.class);
		groupB.set(b);

		// establish binding
		BindingUtil.bindOneWay(a, x -> x.setValueA(groupB.proxy().getValueB()));

		// pull up
		b.valueB = 2;
		groupB.pullUp();
		assertEquals(2, a.valueA);

		// push down, nothing should happen
		a.valueA = 3;
		groupB.pushDown();
		assertEquals(2, b.valueB);
	}

	@Test(expected = RuntimeException.class)
	public void noProxyAccessed() {
		TestA a = new TestA();

		// establish binding
		BindingUtil.bind(a, x -> x.setValueA(4));

	}

	@Test
	public void simpleRO() {
		TestA a = new TestA();
		TestB b = new TestB();

		BindingGroup<TestB> groupB = new BindingGroup<>(TestB.class);
		groupB.set(b);

		// establish binding
		BindingUtil.bind(a, x -> groupB.proxy().setValueB(x.getValueRO()));

		// pull up
		b.valueB = 2;
		groupB.pullUp();
		assertEquals(0, a.valueRO);

		// push down
		a.valueRO = 3;
		groupB.pushDown();
		assertEquals(3, b.valueB);
	}

	@Test
	public void nested() {
		TestA a = new TestA();
		TestB b = new TestB();
		TestC c = new TestC();
		b.setC(c);

		BindingGroup<TestB> groupB = new BindingGroup<>(TestB.class);
		groupB.set(b);

		// establish binding
		BindingUtil
				.bind(a, x -> x.setValueA(groupB.proxy().getC().getValueC()));

		// pull up
		c.valueC = 2;
		groupB.pullUp();
		assertEquals(2, a.valueA);

		// push down
		a.valueA = 3;
		groupB.pushDown();
		assertEquals(3, c.valueC);
	}

	@Test
	public void explicit() {
		TestA a = new TestA();
		TestB b = new TestB();

		BindingGroup<TestB> groupB = new BindingGroup<>(TestB.class);
		groupB.set(b);

		Binding<TestB> binding = new Binding<>();
		binding.setComponent(a);
		binding.setPullUp(data -> a.valueA = data.valueB + 1);
		binding.setPushDown(data -> data.valueB = a.valueA - 1);
		// establish binding
		BindingUtil.bind(() -> groupB.proxy(), binding);

		// pull up
		b.valueB = 2;
		groupB.pullUp();
		assertEquals(3, a.valueA);

		// push down
		a.valueA = 5;
		groupB.pushDown();
		assertEquals(4, b.valueB);
	}

}
