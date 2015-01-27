package com.github.ruediste.laf.core.base.configuration;

import static org.junit.Assert.assertEquals;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import laf.test.DeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.ruediste.laf.core.base.configuration.*;
import com.github.ruediste.laf.core.base.configuration.DefinerConfigurationValueProvider.ExtendedParameterNotDefined;

@RunWith(Arquillian.class)
public class ConfigurationFactoryTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = DeploymentProvider
				.getDefault()
				.addClasses(
						Modules.getAllRequiredClasses(CoreBaseConfigurationModule.class));
		System.out.println(archive.toString(true));
		return archive;
	}

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

	public static class ConfigurationRegisterer {
		@Inject
		TestConfigurationDefinerA definerA;
		@Inject
		TestConfigurationDefinerB definerB;

		void observe(@Observes DiscoverConfigruationEvent e) {
			e.add(definerB);
			e.add(definerA);
		}
	}

	@ApplicationScoped
	public static class TestConfigurationDefinerA implements
			ConfigurationDefiner {

		public void produce(TestConfigurationParameterA val) {
			val.set("FooA");
		}

		public void produce(TestConfigurationParameterB val) {
			val.set("FooA");
		}

		@ExtendConfiguration
		public void produce(TestConfigurationParameterC val) {
			val.set(val.get() + "FooA");
		}

		@ExtendConfiguration
		public void produce(TestConfigurationParameterF val) {
			// access should fail
			val.get();
		}

		private int hCount;

		public void produce(TestConfigurationParameterH val) {
			val.set(String.valueOf(hCount++));
		}

		public int gethCount() {
			return hCount;
		}

	}

	@ApplicationScoped
	public static class TestConfigurationDefinerB implements
			ConfigurationDefiner {
		public void produce(TestConfigurationParameterB val) {
			val.set("FooB");
		}

		public void produce(TestConfigurationParameterC val) {
			val.set("FooB");
		}

		public void produce(TestConfigurationParameterD val) {
			val.set("FooB");
		}

		public void produce(TestConfigurationParameterE val,
				TestConfigurationParameterD theD) {
			val.set("e" + theD.get());
		}
	}

	@Inject
	TestConfigurationDefinerA definerA;

	@Inject
	ConfigurationValue<TestConfigurationParameterA> configValueA;
	@Inject
	ConfigurationValue<TestConfigurationParameterB> configValueB;
	@Inject
	ConfigurationValue<TestConfigurationParameterC> configValueC;
	@Inject
	ConfigurationValue<TestConfigurationParameterD> configValueD;
	@Inject
	ConfigurationValue<TestConfigurationParameterE> configValueE;
	@Inject
	ConfigurationValue<TestConfigurationParameterF> configValueF;
	@Inject
	ConfigurationValue<TestConfigurationParameterG> configValueG;
	@Inject
	ConfigurationValue<TestConfigurationParameterH> configValueH;

	@Test
	public void test() {
		// only defined in A
		assertEquals("FooA", configValueA.value().get());

		// defined in A and B
		assertEquals("FooA", configValueB.value().get());

		// defined using ExtendsConfiguration
		assertEquals("FooBFooA", configValueC.value().get());

		// defined in B only
		assertEquals("FooB", configValueD.value().get());

		// defined using parameter D as input
		assertEquals("eFooB", configValueE.value().get());
	}

	@Test(expected = ExtendedParameterNotDefined.class)
	public void extendsWithUndefinedBaseShouldFail() {
		configValueF.value().get();
	}

	@Test
	public void defaultWorks() {
		assertEquals("def", configValueG.value().get());
	}

	@Test
	public void cacheWorks() {
		assertEquals(0, definerA.gethCount());
		assertEquals("0", configValueH.value().get());
		assertEquals(1, definerA.gethCount());
		assertEquals("0", configValueH.value().get());
		assertEquals(1, definerA.gethCount());
	}
}
