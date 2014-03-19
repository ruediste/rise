package laf.controllerInfo.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class ControllerInfoImplTest {

	@Test
	public void checkNames() {
		ControllerInfoImpl info = new ControllerInfoImpl(TestController.class);
		assertEquals("Test", info.getName());
		assertEquals("laf.controllerInfo.impl", info.getPackage());
		assertEquals("laf.controllerInfo.impl.Test", info.getQualifiedName());
	}
}
