package com.github.ruediste.laf.core.base.configuration;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.ruediste.laf.test.DeploymentProvider;

@RunWith(Arquillian.class)
public class SimpleArquillianTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = DeploymentProvider.getDefault();
		System.out.println(archive.toString(true));
		return archive;
	}

	@Test
	public void test() {
		System.out.println("Hello World");
	}
}
