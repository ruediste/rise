package laf.mvc.actionPath;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import laf.base.Function2;
import laf.core.controllerInfo.ActionMethodInfo;
import laf.core.controllerInfo.ParameterInfo;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.mvc.actionPath.ActionInvocation;
import laf.mvc.actionPath.ActionPath;

import org.junit.Test;

import com.google.common.base.Suppliers;

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
		when(argument.get()).thenReturn(value);
		invocation.getArguments().add(argument);
		path.getElements().add(invocation);

		ActionPath<Object> objectPath = path.map(Suppliers.supplierFunction());

		assertEquals(1, objectPath.getElements().size());
		assertSame(methodInfo, objectPath.getElements().get(0).getMethodInfo());
		assertSame(value, objectPath.getElements().get(0).getArguments().get(0));
	}

	@Test
		public void testMapWithType() throws Exception {
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
					.mapWithType(new Function2<ParameterInfo, String, String>() {
	
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
