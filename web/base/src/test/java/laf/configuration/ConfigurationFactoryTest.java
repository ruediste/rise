package laf.configuration;

import static org.junit.Assert.assertEquals;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import laf.test.BaseDeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConfigurationFactoryTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = BaseDeploymentProvider
				.getDefault()
				.addClasses(
						Modules.getAllRequiredClasses(ConfigurationModule.class))
				.addClasses(TestConfigurationParameterA.class,
						TestConfigurationDefinerA.class);
		System.out.println(archive.toString(true));
		return archive;
	}

	private static interface TestConfigurationParameterA extends
			ConfigurationParameter<String> {
	}

	private static interface TestConfigurationParameterB extends
			ConfigurationParameter<String> {
	}

	private static interface TestConfigurationParameterC extends
			ConfigurationParameter<String> {
	}

	private static interface TestConfigurationParameterD extends
	ConfigurationParameter<String> {
	}

	static class TestConfigurationDefinerA implements ConfigurationDefiner {
		@Inject
		TestConfigurationDefinerB definerB;

		void observe(@Observes DiscoverConfigruationEvent e) {
			e.add(definerB);
			e.add(this);
		}

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

	}

	static class TestConfigurationDefinerB implements ConfigurationDefiner {
		public void produce(TestConfigurationParameterB val) {
			val.set("FooB");
		}

		public void produce(TestConfigurationParameterC val) {
			val.set("FooB");
		}

		public void produce(TestConfigurationParameterD val) {
			val.set("FooB");
		}
	}

	@Inject
	ConfigurationValue<TestConfigurationParameterA> configValueA;

	@Inject
	ConfigurationValue<TestConfigurationParameterB> configValueB;
	@Inject
	ConfigurationValue<TestConfigurationParameterC> configValueC;
	@Inject
	ConfigurationValue<TestConfigurationParameterD> configValueD;

	@Test
	public void test() {
		assertEquals("FooA", configValueA.value().get());
		assertEquals("FooA", configValueB.value().get());
		assertEquals("FooBFooA", configValueC.value().get());
		assertEquals("FooB", configValueD.value().get());
	}
}
