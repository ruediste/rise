package laf.controllerInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.enterprise.inject.spi.BeanManager;

import laf.controllerInfo.impl.EmbeddedTestController;
import laf.controllerInfo.impl.TestController;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;

public class ControllerInfoRepositoryInitializerTest {

	private ControllerInfoRepository repo;
	private ControllerInfoRepositoryInitializer initializer;

	@Before
	public void setup() {
		repo = new ControllerInfoRepository();
		initializer = new ControllerInfoRepositoryInitializer();
		initializer.repository = repo;
		initializer.beanManager = mock(BeanManager.class);
		initializer.log = mock(Logger.class);
	}

	@Test
	public void testCreateNormalController() {

		ControllerInfo info = initializer.createControllerInfo(
				TestController.class, false);

		assertEquals("Test", info.getName());
		assertEquals("laf.controllerInfo.impl", info.getPackage());
		assertEquals("laf.controllerInfo.impl.Test", info.getQualifiedName());

		assertEquals(2, Iterables.size(info.getActionMethodInfos()));

		// check normal method
		ActionMethodInfo methodInfo = info.getActionMethodInfo("actionMethod");
		assertEquals("actionMethod", methodInfo.getName());
		assertSame(info, methodInfo.getControllerInfo());

		assertEquals(1, Iterables.size(methodInfo.getParameters()));
		ParameterInfo parameterInfo = methodInfo.getParameters().iterator()
				.next();
		assertSame(Integer.TYPE, parameterInfo.getType());
		assertSame(methodInfo, parameterInfo.getMethod());

		// check method returning embedded controller
		methodInfo = info.getActionMethodInfo("actionMethodEmbedded");
		assertNotNull(methodInfo);
	}

	@Test
	public void testCreateEmbeddedController() {
		ControllerInfo info = initializer.createControllerInfo(
				EmbeddedTestController.class, true);

		assertEquals(1, Iterables.size(info.getActionMethodInfos()));

	}
}
