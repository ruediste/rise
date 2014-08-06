package laf.controllerInfo;

import static laf.test.MockitoExt.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import laf.controllerInfo.impl.EmbeddedTestController;
import laf.controllerInfo.impl.TestController;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

public class ControllerInfoCreationServiceTest {

	private ControllerInfoCreationService service;
	private Predicate<Class<?>> isEmbeddedController;

	@Before
	public void setup() {
		service = new ControllerInfoCreationService();
		isEmbeddedController = mock(new TypeToken<Predicate<Class<?>>>() {
			private static final long serialVersionUID = 1L;
		});
		when(isEmbeddedController.apply(EmbeddedTestController.class))
		.thenReturn(true);
	}

	@Test
	public void testCreateNormalController() {

		ControllerInfo info = service.createControllerInfo(
				TestController.class, null, isEmbeddedController, null);

		assertEquals("Test", info.getName());
		assertEquals("laf.controllerInfo.impl", info.getPackage());
		assertEquals("laf.controllerInfo.impl.Test", info.getQualifiedName());
		assertFalse(info.isEmbeddedController());

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
		ControllerInfo info = service.createControllerInfo(
				EmbeddedTestController.class, null, isEmbeddedController, null);

		assertEquals(1, Iterables.size(info.getActionMethodInfos()));
		assertTrue(info.isEmbeddedController());
	}
}
