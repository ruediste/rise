package laf.controllerInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.impl.TestController;
import laf.controllerInfo.ControllerInfoImpl;

import org.junit.Test;

public class ControllerInfoImplTest {

	@Test
	public void checkNames() {
		ControllerInfoImpl info = new ControllerInfoImpl(TestController.class,
				false);
		assertEquals("Test", info.getName());
		assertEquals("laf.controllerInfo.impl", info.getPackage());
		assertEquals("laf.controllerInfo.impl.Test", info.getQualifiedName());
	}

	@Test
	public void calculateUnusedMethodName() {
		ControllerInfoImpl info = new ControllerInfoImpl(TestController.class,
				true);
		assertEquals("foo", info.calculateUnusedMethodName("foo"));
		ActionMethodInfo methodInfo = mock(ActionMethodInfo.class);
		when(methodInfo.getName()).thenReturn("foo");
		info.putActionMethodInfo(methodInfo);
		assertNotEquals("foo", info.calculateUnusedMethodName("foo"));
	}
}
