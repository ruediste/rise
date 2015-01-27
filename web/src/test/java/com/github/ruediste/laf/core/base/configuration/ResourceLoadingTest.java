package com.github.ruediste.laf.core.base.configuration;

import java.io.InputStream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test used for debugging arquillian resource loading from classpath.
 */
@RunWith(Arquillian.class)
public class ResourceLoadingTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = ShrinkWrap.create(WebArchive.class)
				.addAsWebInfResource(new StringAsset("Hello World"),
						"classes/test.properties");
		System.out.println(archive.toString(true));
		return archive;

	}

	@Test
	public void testLoadResource() {
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("/test.properties");

		// disabled, used for debugging
		// assertNotNull(stream);
	}
}
