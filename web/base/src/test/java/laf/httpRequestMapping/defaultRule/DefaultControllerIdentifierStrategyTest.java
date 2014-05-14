package laf.httpRequestMapping.defaultRule;

import static org.junit.Assert.*;
import laf.controllerInfo.ControllerInfo;
import laf.controllerInfo.ControllerInfoImpl;
import laf.controllerInfo.impl.TestController;
import laf.httpRequestMapping.defaultRule.DefaultControllerIdentifierStrategy;

import org.junit.Test;

public class DefaultControllerIdentifierStrategyTest {

	@Test
		public void testApply() throws Exception {
			DefaultControllerIdentifierStrategy strategy = new DefaultControllerIdentifierStrategy();
			ControllerInfo info = new ControllerInfoImpl(TestController.class,
					false);
	
			assertEquals("laf/controllerInfo/impl/test",
					strategy.apply(info));
		}

	@Test
	public void testWithPrefix() throws Exception {
		DefaultControllerIdentifierStrategy strategy = new DefaultControllerIdentifierStrategy();
		strategy.basePackage = "laf.controllerInfo";
		assertEquals(
				"impl/test",
				strategy.getControllerIdentifier("laf.controllerInfo.impl.TestController"));
		assertEquals("foo/bar/impl/test",
				strategy.getControllerIdentifier("foo.bar.impl.TestController"));
	}
}