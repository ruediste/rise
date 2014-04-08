package laf.urlMapping;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import laf.DefaultLafConfigurator;
import laf.LAF;
import laf.controllerInfo.impl.TestController;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DefaultUrlMappingRuleTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = ShrinkWrap
				.create(WebArchive.class)
				.addAsLibraries(
						Maven.resolver()
						.loadPomFromFile("pom.xml")
						.resolve("org.slf4j:slf4j-api",
								"com.google.guava:guava")
								.withTransitivity().asFile())
				.addPackages(true, "laf").addClass(TestController.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		System.out.println(archive.toString(true));
		return archive;
	}

	@Inject
	ActionPathFactory factory;

	@Inject
	DefaultUrlMappingRule rule;

	@Inject
	LAF laf;

	@Inject
	DefaultLafConfigurator defaultLafConfigurator;

	@Before
	public void init() {
		if (!laf.isInitialized()) {
			defaultLafConfigurator.configure();
			laf.getUrlMappingRules().clear();
			laf.getUrlMappingRules().add(rule);
			laf.initialize();
		}
	}

	@Test
	public void generate() {
		PathActionResult path = (PathActionResult) factory.createActionPath(
				TestController.class).actionMethod(2);

		assertEquals("laf/controllerInfo/impl/test.actionMethod/2",
				rule.generate(path));
	}

	@Test
	public void parse() {
		ActionPath<ParameterValueProvider> path = rule
				.parse("laf/controllerInfo/impl/test.actionMethod/2");
		ActionPath<Object> objectPath = ActionPath.createObjectActionPath(path);
		assertEquals(1, objectPath.getElements().size());
		ActionInvocation<Object> invocation = objectPath.getElements().get(0);
		assertEquals(TestController.class, invocation.getControllerInfo()
				.getControllerClass());
		assertEquals("actionMethod", invocation.getMethodInfo().getName());
		assertEquals(2, invocation.getArguments().get(0));
	}

}
