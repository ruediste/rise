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
				.addClasses(TestConfigurationParameter.class,
						TestConfigurationDefiner.class);
		System.out.println(archive.toString(true));
		return archive;
	}

	private static interface TestConfigurationParameter extends
			ConfigurationParameter<String> {
	}

	static class TestConfigurationDefiner implements ConfigurationDefiner {
		void observe(@Observes DiscoverConfigruationEvent e) {
			e.add(this);
		}

		public void produce(TestConfigurationParameter val) {
			val.set("Foo");
		}

	}

	@Inject
	ConfigurationValue<TestConfigurationParameter> configValue;

	@Test
	public void test() {
		assertEquals("Foo", configValue.value().get());
	}
}
