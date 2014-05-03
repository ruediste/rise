package laf.actionPath;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import laf.attachedProperties.AttachedProperty;
import laf.controllerInfo.ControllerInfo;
import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.ControllerInfoRepositoryInitializer;
import laf.controllerInfo.impl.EmbeddedTestController;
import laf.controllerInfo.impl.TestController;
import laf.misc.ActionContext;

import org.junit.Before;
import org.junit.Test;

public class ActionPathFactoryTest {

	ActionPathFactory factory;
	ControllerInfo testControllerInfo;
	ControllerInfo embeddedControllerInfo;

	@Before
	public void setup() {
		ControllerInfoRepositoryInitializer initializer = new ControllerInfoRepositoryInitializer();
		factory = new ActionPathFactory();
		factory.controllerInfoRepository = mock(ControllerInfoRepository.class);
		testControllerInfo = initializer.createControllerInfo(
				TestController.class, false);
		embeddedControllerInfo = initializer.createControllerInfo(
				EmbeddedTestController.class, true);
		when(
				factory.controllerInfoRepository
				.getControllerInfo(TestController.class)).thenReturn(
						testControllerInfo);
		when(
				factory.controllerInfoRepository
				.getControllerInfo(EmbeddedTestController.class))
				.thenReturn(embeddedControllerInfo);

	}

	@Test
	public void simple() {
		PathActionResult path = (PathActionResult) factory.buildActionPath()
				.controller(TestController.class).actionMethod(5);
		assertEquals(1, path.getElements().size());
		ActionInvocation<Object> invocation = path.getElements().get(0);
		assertEquals(TestController.class, invocation.getMethodInfo()
				.getControllerInfo().getControllerClass());
		assertEquals("actionMethod", invocation.getMethodInfo().getMethod()
				.getName());
	}

	@Test
	public void settingProperties() {
		AttachedProperty<Integer> property = new AttachedProperty<>();

		PathActionResult path = (PathActionResult) factory.buildActionPath()
				.set(property, 27).controller(TestController.class)
				.actionMethod(5);

		assertEquals(Integer.valueOf(27), property.get(path));
	}

	@Test
	public void embedded() {
		PathActionResult path = (PathActionResult) factory.buildActionPath()
				.controller(TestController.class).actionMethodEmbedded()
				.actionMethodEmbedded();

		assertEquals(2, path.getElements().size());
		ActionInvocation<Object> invocation = path.getElements().get(0);
		assertEquals(TestController.class, invocation.getMethodInfo()
				.getControllerInfo().getControllerClass());
		assertEquals("actionMethodEmbedded", invocation.getMethodInfo()
				.getMethod().getName());
		invocation = path.getElements().get(1);
		assertEquals(EmbeddedTestController.class, invocation.getMethodInfo()
				.getControllerInfo().getControllerClass());
		assertEquals("actionMethodEmbedded", invocation.getMethodInfo()
				.getMethod().getName());
	}

	@Test
	public void embeddedPartial() {
		factory.actionContext = mock(ActionContext.class);
		PathActionResult invokedResult = (PathActionResult) factory
				.buildActionPath().controller(TestController.class)
				.actionMethodEmbedded().actionMethodEmbedded();
		when(factory.actionContext.getInvokedPath()).thenReturn(invokedResult);

		PathActionResult path = (PathActionResult) factory.buildActionPath()
				.controller(EmbeddedTestController.class)
				.actionMethodEmbedded();

		assertEquals(2, path.getElements().size());
		ActionInvocation<Object> invocation = path.getElements().get(0);
		assertEquals(TestController.class, invocation.getMethodInfo()
				.getControllerInfo().getControllerClass());
		assertEquals("actionMethodEmbedded", invocation.getMethodInfo()
				.getMethod().getName());
		invocation = path.getElements().get(1);
		assertEquals(EmbeddedTestController.class, invocation.getMethodInfo()
				.getControllerInfo().getControllerClass());
		assertEquals("actionMethodEmbedded", invocation.getMethodInfo()
				.getMethod().getName());
	}

	@Test(expected = RuntimeException.class)
	public void embeddedPartialNotFound() {
		factory.actionContext = mock(ActionContext.class);
		PathActionResult invokedResult = (PathActionResult) factory
				.buildActionPath().controller(TestController.class)
				.actionMethod(2);
		when(factory.actionContext.getInvokedPath()).thenReturn(invokedResult);

		factory.buildActionPath().controller(EmbeddedTestController.class)
		.actionMethodEmbedded();
	}
}
