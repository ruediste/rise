package laf.configuration;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import laf.test.BaseDeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

@RunWith(Arquillian.class)
public class ConfigurationFactoryBaseTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = BaseDeploymentProvider
				.getDefault()
				.addClasses(
						Modules.getAllRequiredClasses(ConfigurationModule.class))
						.addClass(TestConfiguredBean.class)
						.addClass(TestBeanA.class)
						.addClass(ITestBeanB.class)
						.addClass(TestBeanB1.class)
						.addClass(TestBeanB2.class)
						.addClass(TestQualifier.class)
						.addAsWebInfResource(
								new StringAsset(
										"laf.configuration.TestConfiguredBean.testBeanA=laf.configuration.TestBeanA\n"
												+ "laf.configuration.TestConfiguredBean.testBeanB=laf.configuration.TestQualifier:laf.configuration.TestBeanB\n"),
						"configuration.properties");
		System.out.println(archive.toString(true));
		return archive;
	}

	@Inject
	TestConfiguredBean bean;

	@Test
	public void test() {
		assertEquals("foo", bean.string);
		assertEquals(4, bean.integer);
		assertEquals(Double.class, bean.clazz);
		assertEquals(TestBeanA.class, bean.testBeanA.get().getClass());
		assertEquals(TestBeanB1.class, bean.testBeanB1.get().getClass());
		assertEquals(TestBeanB1.class, Iterables.get(bean.testBeanBs, 0)
				.getClass());
		assertEquals(TestBeanB2.class, Iterables.get(bean.testBeanBs, 1)
				.getClass());
	}
}
