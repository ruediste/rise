package laf.urlMapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayDeque;

import javax.inject.Inject;

import laf.LAF;
import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathFactory;
import laf.actionPath.PathActionResult;
import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.impl.TestController;
import laf.test.DeploymentProvider;
import laf.urlMapping.parameterHandler.ParameterValueProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DefaultUrlMappingRuleTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = DeploymentProvider
				.getDefault()
				.addClasses(
						Modules.getAllRequiredClasses(UrlMappingModule.class))
						.addClass(TestController.class);
		System.out.println(archive.toString(true));
		return archive;
	}

	@Inject
	ActionPathFactory factory;

	@Inject
	LAF laf;

	@Inject
	UrlMapping urlMapping;

	@Inject
	DefaultUrlMappingRule rule;

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	@Before
	public void init() {
		if (!laf.isInitialized()) {
			ArrayDeque<UrlMappingRule> rules = urlMapping.urlMappingRules
					.getValue();
			rules.clear();
			rules.add(rule);
			laf.initialize();
		}
	}

	@Test
	public void controllerInfoPresent() {
		assertNotNull(controllerInfoRepository
				.getControllerInfo(TestController.class));
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
		ActionPath<Object> objectPath = UrlMapping.createObjectActionPath(path);
		assertEquals(1, objectPath.getElements().size());
		ActionInvocation<Object> invocation = objectPath.getElements().get(0);
		assertEquals(TestController.class, invocation.getControllerInfo()
				.getControllerClass());
		assertEquals("actionMethod", invocation.getMethodInfo().getName());
		assertEquals(2, invocation.getArguments().get(0));
	}

}
