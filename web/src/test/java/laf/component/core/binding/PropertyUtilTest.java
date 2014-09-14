package laf.component.core.binding;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import laf.component.core.binding.ProxyManger.MethodInvocation;
import laf.core.base.Pair;
import laf.core.base.Val;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import org.apache.commons.beanutils.LazyDynaBean;
import org.junit.Test;

import com.google.common.base.Defaults;

public class PropertyUtilTest {

	static class TestBean {

		private int mouthWidth = 90;
		private int[] testGrades = new int[10];

		public int getMouthWidth() {
			return mouthWidth;
		}

		public void setMouthWidth(int mw) {
			mouthWidth = mw;
		}

		public int getTestGrades(int index) {
			return testGrades[index];
		}

		public void setTestGrades(int index, int grade) {
			testGrades[index] = grade;
		}
	}

	@Test
	public void simple() {
		assertEquals(Pair.of("mouthWidth", true),
				PropertyUtil.getPropertyInfo(getInvocation(TestBean.class,
						b -> b.getMouthWidth())));
		assertEquals(Pair.of("mouthWidth", false),
				PropertyUtil.getPropertyInfo(getInvocation(TestBean.class,
						b -> b.setMouthWidth(1))));
	}

	@Test
	public void indexed() {
		assertEquals(Pair.of("testGrades[2]", true),
				PropertyUtil.getPropertyInfo(getInvocation(TestBean.class,
						b -> b.getTestGrades(2))));
		assertEquals(Pair.of("testGrades[2]", false),
				PropertyUtil.getPropertyInfo(getInvocation(TestBean.class,
						b -> b.setTestGrades(2, 3))));
	}

	@Test
	public void dynaSimple() {

		assertEquals(Pair.of("foo", true),
				PropertyUtil.getPropertyInfo(getInvocation(LazyDynaBean.class,
						b -> b.get("foo"))));
		assertEquals(Pair.of("foo", false),
				PropertyUtil.getPropertyInfo(getInvocation(LazyDynaBean.class,
						b -> b.set("foo", null))));

	}

	@Test
	public void dynaIndexed() {

		assertEquals(Pair.of("foo[1]", true),
				PropertyUtil.getPropertyInfo(getInvocation(LazyDynaBean.class,
						b -> b.get("foo", 1))));
		assertEquals(Pair.of("foo[1]", false),
				PropertyUtil.getPropertyInfo(getInvocation(LazyDynaBean.class,
						b -> b.set("foo", 1, null))));

	}

	@Test
	public void dynaMapped() {
		assertEquals(Pair.of("foo(bar)", true),
				PropertyUtil.getPropertyInfo(getInvocation(LazyDynaBean.class,
						b -> b.get("foo", "bar"))));
		assertEquals(Pair.of("foo(bar)", false),
				PropertyUtil.getPropertyInfo(getInvocation(LazyDynaBean.class,
						b -> b.set("foo", "bar", null))));

	}

	@SuppressWarnings("unchecked")
	<T> MethodInvocation getInvocation(Class<T> cls, Consumer<T> expression) {
		Val<MethodInvocation> invocation = new Val<>();
		Enhancer e = new Enhancer();
		e.setSuperclass(cls);
		e.setCallback(new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				invocation.set(new MethodInvocation(method, args));
				return Defaults.defaultValue(method.getReturnType());
			}
		});
		expression.accept((T) e.create());
		return invocation.get();
	}
}
