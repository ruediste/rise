package laf.testApp;

import static org.junit.Assert.assertEquals;
import laf.testApp.smokeTest.SmokeTestController;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@RunWith(Arquillian.class)
public class SmokeTest extends TestBase {

	@Deployment
	public static WebArchive createDeployment() {
		//@formatter:off
		WebArchive result = ShrinkWrap.create(WebArchive.class, "testApp.war")
			.merge(ShrinkWrap.create(GenericArchive.class)
				.as(ExplodedImporter.class)
				.importDirectory("src/main/webapp")
				.as(GenericArchive.class))
			.merge(ShrinkWrap.create(GenericArchive.class)
					.as(ExplodedImporter.class)
					.importDirectory("target/classes")
					.as(GenericArchive.class), "WEB-INF/classes")
			.addAsLibraries(
				Maven.resolver()
				.loadPomFromFile("pom.xml")
				.resolve("ch.laf:laf-web")
				.withoutTransitivity().asFile()
				);
		//@formatter:on
		System.out.println(result.toString(true));
		return result;

	}

	@Drone
	private WebDriver browser;

	@Test
	public void should_login_successfully() {
		browser.get(url(util.mwServletPath(util.mwPath(
				SmokeTestController.class).index())));

		assertEquals("Smoke Passed", browser
				.findElement(By.cssSelector("body")).getText());
	}
}
