package laf.http.requestMapping.defaultRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import laf.actionPath.*;
import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.impl.TestController;
import laf.defaultConfiguration.DefaultConfigurationModule;
import laf.http.request.HttpRequestImpl;
import laf.http.requestMapping.HttpRequestMappingService;
import laf.http.requestMapping.defaultRule.DefaultHttpRequestMappingModule;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.ObjectActionPathProducer;
import laf.test.DeploymentProvider;
import laf.test.TestConfigurationFactory;

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
		WebArchive archive = DeploymentProvider
				.getDefault()
				.addClasses(
						Modules.getAllRequiredClasses(DefaultHttpRequestMappingModule.class))
				.addClasses(
						Modules.getAllRequiredClasses(DefaultConfigurationModule.class))
				.addClasses(TestController.class,
						ObjectActionPathProducer.class,
						TestConfigurationFactory.class);
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
