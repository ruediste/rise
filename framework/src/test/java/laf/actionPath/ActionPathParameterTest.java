package laf.actionPath;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ActionPathParameterTest {

	@Test
	public void test() {
		ActionPath<String> path = new ActionPath<>();
		ActionPathParameter parameter = new ActionPathParameter("test");
		parameter.set(path, "Hello");
		assertEquals("Hello", parameter.get(path));
	}
}
