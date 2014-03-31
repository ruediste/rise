package laf.controllerInfo.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import laf.controllerInfo.ActionMethodInfo;

import org.junit.Test;

public class ControllerInfoImplTest {

	@Test
	public void checkNames() {
		ControllerInfoImpl info = new ControllerInfoImpl(TestController.class);
		assertEquals("Test", info.getName());
		assertEquals("laf.controllerInfo.impl", info.getPackage());
		assertEquals("laf.controllerInfo.impl.Test", info.getQualifiedName());
	}

	@Test
	public void calculateUnusedMethodName() {
		ControllerInfoImpl info = new ControllerInfoImpl(TestController.class);
		assertEquals("foo", info.calculateUnusedMethodName("foo"));
		ActionMethodInfo methodInfo = mock(ActionMethodInfo.class);
		when(methodInfo.getName()).thenReturn("foo");
		info.putActionMethodInfo(methodInfo);
		assertNotEquals("foo", info.calculateUnusedMethodName("foo"));
	}
}
