package laf.initializer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import laf.controllerInfo.impl.TestController;

import org.junit.Test;

public class InitializerMatcherTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test() {
		Initializer init = mock(Initializer.class);
		when(init.getComponentClass()).thenReturn((Class) TestController.class);
		InitializerMatcher matcher = new InitializerMatcher(
				TestController.class);

		assertTrue(matcher.matches(init));

		when(init.getId()).thenReturn("foo");
		assertTrue(matcher.matches(init));

		matcher = new InitializerMatcher(TestController.class, "bar");
		assertFalse(matcher.matches(init));

		when(init.getId()).thenReturn("bar");
		assertTrue(matcher.matches(init));
	}
}
