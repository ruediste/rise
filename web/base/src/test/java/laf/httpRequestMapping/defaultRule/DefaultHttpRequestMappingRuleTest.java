package laf.httpRequestMapping.defaultRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathFactory;
import laf.actionPath.PathActionResult;
import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.impl.TestController;
import laf.httpRequest.HttpRequestImpl;
import laf.httpRequestMapping.HttpRequestMappingService;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.test.BaseDeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Suppliers;

@RunWith(Arquillian.class)
public class DefaultHttpRequestMappingRuleTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = BaseDeploymentProvider
				.getDefault()
				.addClasses(
						Modules.getAllRequiredClasses(DefaultHttpRequestMappingModule.class))
						.addClass(TestController.class);
		System.out.println(archive.toString(true));
		return archive;
	}

	@Inject
	ActionPathFactory factory;

	@Inject
	HttpRequestMappingService httpRequestMappingService;

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	@Inject
	DefaultHttpRequestMappingModule defaultHttpRequestMappingModule;

	@Test
	public void controllerInfoPresent() {
		assertNotNull(controllerInfoRepository
				.getControllerInfo(TestController.class));
	}

	@Test
	public void generate() {
		PathActionResult path = (PathActionResult) factory
				.buildActionPath(new ActionPath<Object>())
				.controller(TestController.class).actionMethod(2);

		assertEquals(new HttpRequestImpl(
				"laf/controllerInfo/impl/test.actionMethod/2"),
				httpRequestMappingService.generate(path));
	}

	@Test
	public void parse() {
		ActionPath<ParameterValueProvider> path = httpRequestMappingService
				.parse(new HttpRequestImpl(
						"laf/controllerInfo/impl/test.actionMethod/2"));
		ActionPath<Object> objectPath = path.map(Suppliers.supplierFunction());
		assertEquals(1, objectPath.getElements().size());
		ActionInvocation<Object> invocation = objectPath.getElements().get(0);
		assertEquals(TestController.class, invocation.getControllerInfo()
				.getControllerClass());
		assertEquals("actionMethod", invocation.getMethodInfo().getName());
		assertEquals(2, invocation.getArguments().get(0));
	}

}
