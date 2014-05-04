package laf.configuration;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import laf.test.DeploymentProvider;

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
		WebArchive archive = DeploymentProvider
				.getDefault()
				.addClasses(
						Modules.getAllRequiredClasses(ConfigurationModule.class))
						.addClass(TestConfiguredBean.class);
		return archive;
	}

	@Inject
	TestConfiguredBean bean;

	@Test
	public void test() {
		assertEquals("foo", bean.string);
		assertEquals(4, bean.integer);
		assertEquals(Double.class, bean.clazz);
	}
}
