package laf.testApp;

import java.net.URL;

import javax.inject.Inject;

import laf.integration.IntegrationUtil;

import org.jboss.arquillian.test.api.ArquillianResource;

public class TestBase {
	@Inject
	protected IntegrationUtil util;

	@ArquillianResource
	private URL deploymentUrl;

	protected String url(String servletPath) {
		return deploymentUrl + servletPath;
	}
}
