package laf.component.core.beanutil;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.reflect.TypeToken;

@RunWith(MockitoJUnitRunner.class)
public class BeanUtilTest {

	@InjectMocks
	BeanUtil util;

	static class TestBean {
		private int value;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}

	@Test
	public void testExtractProperty() throws Exception {
		BeanProperty p = util.extractProperty(TypeToken.of(TestBean.class),
				x -> x.getValue());
		assertEquals(TestBean.class, p.getStartClass());
		assertEquals(1, p.getPath().size());
	}
}
