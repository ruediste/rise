package laf.initialization;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import laf.controllerInfo.impl.TestController;
import laf.initialization.Initializer;
import laf.initialization.InitializerMatcher;

import org.junit.Test;

public class InitializerMatcherTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test() {
		Initializer init = mock(Initializer.class);
		when(init.getRepresentingClass()).thenReturn(
				(Class) TestController.class);
		InitializerMatcher matcher = new InitializerMatcher(
				TestController.class);

		assertTrue(matcher.matches(init));

	}
}
