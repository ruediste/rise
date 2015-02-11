package com.github.ruediste.laf.core.base.configuration;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.laf.core.base.configuration.DefinerConfigurationValueProvider.ExtendedParameterNotDefined;
import com.github.ruediste.laf.core.guice.LoggerBindingModule;
import com.github.ruediste.laf.core.guice.PostConstructModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

public class ConfigurationFactoryTest {

	public static interface TestConfigurationParameterA extends
			ConfigurationParameter<String> {
	}

	public static interface TestConfigurationParameterB extends
			ConfigurationParameter<String> {
	}

	public static interface TestConfigurationParameterC extends
			ConfigurationParameter<String> {
	}

	public static interface TestConfigurationParameterD extends
			ConfigurationParameter<String> {
	}

	public static interface TestConfigurationParameterE extends
			ConfigurationParameter<String> {
	}

	public static interface TestConfigurationParameterF extends
			ConfigurationParameter<String> {
	}

	@ConfigurationDefault("def")
	public static interface TestConfigurationParameterG extends
			ConfigurationParameter<String> {
	}

	public static interface TestConfigurationParameterH extends
			ConfigurationParameter<String> {
	}

	public static class TestConfigurationDefinerA implements
			ConfigurationDefiner {

		public TestConfigurationParameterA produce() {
			return () -> "FooA";
		}

		public TestConfigurationParameterB b() {
			return () -> "FooA";
		}

		public TestConfigurationParameterC c(TestConfigurationParameterC val) {
			return () -> val.get() + "FooA";
		}

		public TestConfigurationParameterF f(TestConfigurationParameterF val) {
			// access should fail
			val.get();
			return () -> null;
		}

		private int hCount;

		public TestConfigurationParameterH h() {
			return () -> String.valueOf(hCount++);
		}

		public int gethCount() {
			return hCount;
		}

	}

	public static class TestConfigurationDefinerB implements
			ConfigurationDefiner {
		public TestConfigurationParameterB b() {
			return () -> "FooB";
		}

		public TestConfigurationParameterC c() {
			return () -> "FooB";
		}

		public TestConfigurationParameterD d() {
			return () -> "FooB";
		}

		public TestConfigurationParameterE e(TestConfigurationParameterD theD) {
			return () -> "e" + theD.get();
		}
	}

	@Inject
	ConfigurationFactory factory;

	@Before
	public void setup() {
		Guice.createInjector(new LoggerBindingModule(),
				new PostConstructModule(), new CoreBaseConfigurationModule(),
				new AbstractModule() {

					@Override
					protected void configure() {
					}

				}).injectMembers(this);
		factory.add(TestConfigurationDefinerB.class).add(
				TestConfigurationDefinerA.class);
	}

	@Inject
	TestConfigurationDefinerA definerA;

	TestConfigurationParameterA configValueA;
	TestConfigurationParameterB configValueB;
	TestConfigurationParameterC configValueC;
	TestConfigurationParameterD configValueD;
	TestConfigurationParameterE configValueE;
	TestConfigurationParameterF configValueF;
	TestConfigurationParameterG configValueG;
	TestConfigurationParameterH configValueH;

	@Test
	public void test() {
		// only defined in A
		assertEquals("FooA", configValueA.get());

		// defined in A and B
		assertEquals("FooA", configValueB.get());

		// defined using ExtendsConfiguration
		assertEquals("FooBFooA", configValueC.get());

		// defined in B only
		assertEquals("FooB", configValueD.get());

		// defined using parameter D as input
		assertEquals("eFooB", configValueE.get());
	}

	@Test(expected = ExtendedParameterNotDefined.class)
	public void extendsWithUndefinedBaseShouldFail() {
		configValueF.get();
	}

	@Test
	public void defaultWorks() {
		assertEquals("def", configValueG.get());
	}

	@Test
	public void cacheWorks() {
		assertEquals(0, definerA.gethCount());
		assertEquals("0", configValueH.get());
		assertEquals(1, definerA.gethCount());
		assertEquals("0", configValueH.get());
		assertEquals(1, definerA.gethCount());
	}
}
