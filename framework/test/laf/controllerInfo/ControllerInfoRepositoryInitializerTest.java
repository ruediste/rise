package laf.controllerInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.enterprise.inject.spi.BeanManager;

import laf.controllerInfo.impl.TestController;

import org.junit.Test;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;

public class ControllerInfoRepositoryInitializerTest {

	@Test
	public void testCreateNormalController() {
		ControllerInfoRepository repo = new ControllerInfoRepository();
		ControllerInfoRepositoryInitializer initializer = new ControllerInfoRepositoryInitializer();
		initializer.repository = repo;
		initializer.beanManager = mock(BeanManager.class);
		initializer.log = mock(Logger.class);

		ControllerInfo info = initializer
				.createControllerInfo(TestController.class);

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
}
