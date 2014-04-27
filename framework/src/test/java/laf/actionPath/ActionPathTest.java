package laf.actionPath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import laf.controllerInfo.ActionMethodInfo;
import laf.urlMapping.UrlMappingModule;
import laf.urlMapping.parameterValueProvider.ParameterValueProvider;

import org.junit.Test;

public class ActionPathTest {

	@Test
	public void testCreateObjectActionPath() throws Exception {

		ActionPath<ParameterValueProvider> path = new ActionPath<>();
		ActionInvocation<ParameterValueProvider> invocation = new ActionInvocation<>();
		ActionMethodInfo methodInfo = mock(ActionMethodInfo.class);
		invocation.setMethodInfo(methodInfo);
		ParameterValueProvider argument = mock(ParameterValueProvider.class);
		Object value = new Object();
		when(argument.provideValue()).thenReturn(value);
		invocation.getArguments().add(argument);
		path.getElements().add(invocation);

		ActionPath<Object> objectPath = UrlMappingModule
				.createObjectActionPath(path);

		assertEquals(1, objectPath.getElements().size());
		assertSame(methodInfo, objectPath.getElements().get(0).getMethodInfo());
		assertSame(value, objectPath.getElements().get(0).getArguments().get(0));
	}

}
