package laf.controllerInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import laf.controllerInfo.impl.TestController;

import org.junit.Test;

public class ControllerInfoImplTest {

	@Test
	public void checkNames() {
		ControllerInfoImpl info = new ControllerInfoImpl(TestController.class,
				null, false);
		assertEquals("Test", info.getName());
		assertEquals("laf.controllerInfo.impl", info.getPackage());
		assertEquals("laf.controllerInfo.impl.Test", info.getQualifiedName());
	}

	@Test
	public void calculateUnusedMethodName() {
		ControllerInfoImpl info = new ControllerInfoImpl(TestController.class,
				null, false);
		assertEquals("foo", info.calculateUnusedMethodName("foo"));
		ActionMethodInfo methodInfo = mock(ActionMethodInfo.class);
		when(methodInfo.getName()).thenReturn("foo");
		info.putActionMethodInfo(methodInfo);
		assertNotEquals("foo", info.calculateUnusedMethodName("foo"));
	}
}
