package laf.actionPath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import laf.Function2;
import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.ParameterInfo;
import laf.httpRequestMapping.HttpRequestMappingService;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

import org.junit.Test;

public class ActionPathTest {

	@Test
	public void testCreateObjectActionPath() throws Exception {

		ActionPath<ParameterValueProvider> path = new ActionPath<>();
		ActionInvocation<ParameterValueProvider> invocation = new ActionInvocation<>();
		ActionMethodInfo methodInfo = mock(ActionMethodInfo.class);
		ParameterInfo parameterInfo = mock(ParameterInfo.class);
		when(methodInfo.getParameters()).thenReturn(
				Arrays.asList(parameterInfo));

		invocation.setMethodInfo(methodInfo);
		ParameterValueProvider argument = mock(ParameterValueProvider.class);
		Object value = new Object();
		when(argument.provideValue()).thenReturn(value);
		invocation.getArguments().add(argument);
		path.getElements().add(invocation);

		ActionPath<Object> objectPath = HttpRequestMappingService
				.createObjectActionPath(path);

		assertEquals(1, objectPath.getElements().size());
		assertSame(methodInfo, objectPath.getElements().get(0).getMethodInfo());
		assertSame(value, objectPath.getElements().get(0).getArguments().get(0));
	}

	@Test
	public void testMapWithParameter() throws Exception {
		ActionPath<String> path = new ActionPath<String>();
		ActionInvocation<String> invocation = new ActionInvocation<>();
		path.getElements().add(invocation);
		invocation.getArguments().add("foo");

		ActionMethodInfo methodInfo = mock(ActionMethodInfo.class);
		invocation.setMethodInfo(methodInfo);

		ParameterInfo parameterInfo = mock(ParameterInfo.class);
		when(methodInfo.getParameters()).thenReturn(
				Arrays.asList(parameterInfo));

		ActionPath<String> result = path
				.mapWithParameter(new Function2<ParameterInfo, String, String>() {

					@Override
					public String apply(ParameterInfo a, String b) {
						assertEquals("foo", b);
						return "bar";
					}
				});

		assertEquals(1, result.getElements().size());
		assertSame(methodInfo, result.getElements().get(0).getMethodInfo());
		assertSame("bar", result.getElements().get(0).getArguments().get(0));

	}
}
