package com.github.ruediste.laf.test;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.servlet.Servlet;

import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.integration.StandaloneLafApplication;
import com.github.ruediste.laf.mvc.web.IControllerMvcWeb;

public abstract class WebTestBase {

	@Inject
	IntegrationTestUtil util;

	protected String url(ActionResult result) {
		return util.url(result);
	}

	protected <T extends IControllerMvcWeb> T path(Class<T> controllerClass) {
		return util.go(controllerClass);
	}

	private AtomicBoolean started = new AtomicBoolean(false);

	@Before
	public void beforeWebTestBase() {
		if (started.getAndSet(true))
			return;

		Servlet frontServlet = createServlet(this);

		String baseUrl = new StandaloneLafApplication().startForTesting(
				frontServlet, 0);
		util.initialize(baseUrl);
	}

	/**
	 * Create the servlet for the integration tests
	 */
	protected abstract Servlet createServlet(Object testCase);

	protected WebDriver newDriver() {
		HtmlUnitDriver driver = new HtmlUnitDriver(true);
		return driver;
	}

}
