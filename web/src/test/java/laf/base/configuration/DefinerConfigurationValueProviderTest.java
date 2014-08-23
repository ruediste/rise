package laf.base.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import laf.base.Val;
import laf.base.configuration.ConfigurationDefiner;
import laf.base.configuration.ConfigurationModule;
import laf.base.configuration.DefinerConfigurationValueProvider;
import laf.test.DeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.reflect.TypeToken;

@RunWith(Arquillian.class)
public class DefinerConfigurationValueProviderTest {
	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = DeploymentProvider
				.getDefault()
				.addClasses(
						Modules.getAllRequiredClasses(ConfigurationModule.class))
						.addClasses(ITestBean.class, TestBean.class);
		return archive;
	}

	public static class TestDefiner implements ConfigurationDefiner {
		public void produce(TestConfigurationParameter value) {
			value.set("Hello");
		}
	}

	@Inject
	DefinerConfigurationValueProvider provider;

	@Inject
	TestDefiner definer;

	@Test
	public void test() {
		provider.setDefiner(definer);
		Val<String> value = provider.provideValue(TestConfigurationParameter.class,
				TypeToken.of(String.class));
		assertNotNull(value);
		assertEquals("Hello", value.get());
	}
}