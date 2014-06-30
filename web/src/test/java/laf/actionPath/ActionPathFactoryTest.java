package laf.actionPath;

import static laf.test.MockitoExt.mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import laf.base.attachedProperties.AttachedProperty;
import laf.controllerInfo.*;
import laf.controllerInfo.impl.EmbeddedTestController;
import laf.controllerInfo.impl.TestController;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;

public class ActionPathFactoryTest {

	ActionPathFactory factory;
	ControllerInfo testControllerInfo;
	ControllerInfo embeddedControllerInfo;

	@Before
	public void setup() {
		ControllerInfoService service = new ControllerInfoService();
		Predicate<Class<?>> isEmbeddedController = mock(new TypeToken<Predicate<Class<?>>>() {
			private static final long serialVersionUID = 1L;
		});
		when(isEmbeddedController.apply(EmbeddedTestController.class))
		.thenReturn(true);

		factory = new ActionPathFactory();
		factory.controllerInfoRepository = mock(ControllerInfoRepository.class);
		testControllerInfo = service.createControllerInfo(TestController.class,
				null, isEmbeddedController, null);
		embeddedControllerInfo = service.createControllerInfo(
				EmbeddedTestController.class, null, isEmbeddedController, null);
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
		PathActionResult path = (PathActionResult) factory
				.buildActionPath(new ActionPath<Object>())
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
		AttachedProperty<ActionPath<?>, Integer> property = new AttachedProperty<>();

		PathActionResult path = (PathActionResult) factory
				.buildActionPath(new ActionPath<Object>()).set(property, 27)
				.controller(TestController.class).actionMethod(5);

		assertEquals(Integer.valueOf(27), property.get(path));
	}

	@Test
	public void embedded() {
		PathActionResult path = (PathActionResult) factory
				.buildActionPath(new ActionPath<Object>())
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
		PathActionResult invokedPath = (PathActionResult) factory
				.buildActionPath(new ActionPath<Object>())
				.controller(TestController.class).actionMethodEmbedded()
				.actionMethodEmbedded();

		PathActionResult path = (PathActionResult) factory
				.buildActionPath(invokedPath)
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
		PathActionResult currentPath = (PathActionResult) factory
				.buildActionPath(new ActionPath<Object>())
				.controller(TestController.class).actionMethod(2);

		factory.buildActionPath(currentPath)
		.controller(EmbeddedTestController.class)
				.actionMethodEmbedded();
	}
}
