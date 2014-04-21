package laf.initialization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SingletonInitializersTest {
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
								.addClasses(Modules.getClasses(InitializationModule.class))
								.addClass(TestInitializer.class)
								.addClass(TestRootInitializer.class)
								.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
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
		initializationService.initialize(TestRootInitializer.class);
		assertTrue(testInitializer.initialized);
	}
}
