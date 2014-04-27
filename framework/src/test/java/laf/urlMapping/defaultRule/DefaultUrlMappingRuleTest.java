package laf.urlMapping.defaultRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import laf.LAF;
import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathFactory;
import laf.actionPath.PathActionResult;
import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.impl.TestController;
import laf.test.DeploymentProvider;
import laf.urlMapping.UrlMappingModule;
import laf.urlMapping.parameterValueProvider.ParameterValueProvider;

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
						Modules.getAllRequiredClasses(DefaultUrlMappingRuleModule.class))
				.addClass(TestController.class);
		System.out.println(archive.toString(true));
		return archive;
	}

	@Inject
	ActionPathFactory factory;

	@Inject
	LAF laf;

	@Inject
	UrlMappingModule urlMapping;

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	@Before
	public void init() {
		if (!laf.isInitialized()) {
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
				urlMapping.generate(path));
	}

	@Test
	public void parse() {
		ActionPath<ParameterValueProvider> path = urlMapping
				.parse("laf/controllerInfo/impl/test.actionMethod/2");
		ActionPath<Object> objectPath = UrlMappingModule
				.createObjectActionPath(path);
		assertEquals(1, objectPath.getElements().size());
		ActionInvocation<Object> invocation = objectPath.getElements().get(0);
		assertEquals(TestController.class, invocation.getControllerInfo()
				.getControllerClass());
		assertEquals("actionMethod", invocation.getMethodInfo().getName());
		assertEquals(2, invocation.getArguments().get(0));
	}

}
