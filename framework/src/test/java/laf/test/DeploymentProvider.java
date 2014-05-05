package laf.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class DeploymentProvider {

	public static WebArchive getDefault() {
		return ShrinkWrap
				.create(WebArchive.class)
				.addAsLibraries(
						Maven.resolver()
								.loadPomFromFile("pom.xml")
								.resolve("org.slf4j:slf4j-api",
										"com.google.guava:guava")
								.withTransitivity().asFile())
								.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
								.addClass(LoggerProducer.class);
	}
}
