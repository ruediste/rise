package laf.core.http.requestMapping.defaultRule;

import static org.junit.Assert.assertEquals;
import laf.controllerInfo.impl.TestController;
import laf.core.base.configuration.ConfigurationValueImpl;
import laf.core.controllerInfo.ControllerInfo;
import laf.core.controllerInfo.ControllerInfoImpl;
import laf.core.http.requestMapping.defaultRule.BasePackage;
import laf.core.http.requestMapping.defaultRule.DefaultControllerIdentifierStrategy;

import org.junit.Before;
import org.junit.Test;

public class DefaultControllerIdentifierStrategyTest {

	private DefaultControllerIdentifierStrategy strategy;

	@Before
	public void setup() {
		strategy = new DefaultControllerIdentifierStrategy();
		strategy.basePackage = new ConfigurationValueImpl<BasePackage>(
				new BasePackage() {
					@Override
					public void set(String value) {
					}

					@Override
					public String get() {
						return null;
					}
				});
	}

	@Test
	public void testApply() throws Exception {
		ControllerInfo info = new ControllerInfoImpl(TestController.class,
				null, false);

		assertEquals("laf/controllerInfo/impl/test", strategy.apply(info));
	}

	@Test
	public void testWithPrefix() throws Exception {
		strategy.basePackage = new ConfigurationValueImpl<BasePackage>(
				new BasePackage() {

					@Override
					public void set(String value) {
					}

					@Override
					public String get() {
						return "laf.controllerInfo";
					}
				});
		assertEquals(
				"impl/test",
				strategy.getControllerIdentifier("laf.controllerInfo.impl.TestController"));
		assertEquals("foo/bar/impl/test",
				strategy.getControllerIdentifier("foo.bar.impl.TestController"));
	}
}