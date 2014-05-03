package laf.initialization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import laf.test.DeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SingletonInitializersTest {
	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = DeploymentProvider.getDefault()
				.addClasses(Modules.getClasses(InitializationModule.class))
				.addClass(TestInitializer.class)
				.addClass(TestRootInitializer.class);
		System.out.println(archive.toString(true));
		return archive;
	}

	@Inject
	InitializationService initializationService;

	@Inject
	TestInitializer testInitializer;

	@Test
	public void test() {
		assertFalse(testInitializer.initialized);
		initializationService.initialize(DefaultPhase.class,
				TestRootInitializer.class);
		assertTrue(testInitializer.initialized);
	}
}
