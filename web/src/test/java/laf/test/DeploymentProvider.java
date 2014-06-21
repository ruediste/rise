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
								"com.google.guava:guava:16.0.1")
								.withTransitivity().asFile())
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(TestLoggerProducer.class)
				.addAsResource("test-log4j.properties", "log4j.properties");
	}

	public static WebArchive getPersistence() {
		return DeploymentProvider
				.getDefault()
				.addAsResource("test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsResource("test-openejb.xml", "META-INF/openejb.xml")
				.addClasses(TestEntityManagerProducer.class);
	}
}
