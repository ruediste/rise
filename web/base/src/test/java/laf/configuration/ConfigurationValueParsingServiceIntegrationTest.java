package laf.configuration;

import javax.inject.Inject;

import laf.test.BaseDeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.reflect.TypeToken;

@RunWith(Arquillian.class)
public class ConfigurationValueParsingServiceIntegrationTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = BaseDeploymentProvider
				.getDefault()
				.addClasses(
						Modules.getAllRequiredClasses(ConfigurationModule.class))
				.addClasses(ITestBean.class, TestBean.class);
		return archive;
	}

	@Inject
	ConfigurationValueParsingService service;

	@Test
	public void test() {
		service.parse(TypeToken.of(ITestBean.class), TestBean.class.getName());
	}
}
