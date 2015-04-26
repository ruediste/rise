package com.github.ruediste.laf.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.front.DynamicApplication;
import com.github.ruediste.laf.core.front.DynamicApplicationBase;
import com.github.ruediste.laf.core.front.FrontServletBase;
import com.github.ruediste.laf.integration.DynamicIntegrationModule;
import com.github.ruediste.laf.integration.PermanentIntegrationModule;
import com.github.ruediste.laf.integration.StandaloneLafApplication;
import com.github.ruediste.laf.mvc.web.IControllerMvcWeb;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class WebTestBase {

	class FrontServlet extends FrontServletBase {
		private static final long serialVersionUID = 1L;

		public FrontServlet(DynamicApplication fixedApplicationInstance) {
			super(fixedApplicationInstance);

		}

		@Override
		protected void initImpl() throws Exception {
			Salta.createInjector(
					new PermanentIntegrationModule(getServletConfig()))
					.injectMembers(this);
		}

	}

	class TestDynamicApplication extends DynamicApplicationBase {

		@Override
		protected void startImpl(Injector permanentInjector) {
			Injector injector = Salta
					.createInjector(new DynamicIntegrationModule(
							permanentInjector));
			injector.injectMembers(this);
			injector.injectMembers(WebTestBase.this);
		}

	}

	@Inject
	IntegrationTestUtil util;

	protected String url(ActionResult result) {
		return util.url(result);
	}

	protected <T extends IControllerMvcWeb> T path(Class<T> controllerClass) {
		return util.path(controllerClass);
	}

	static AtomicBoolean started = new AtomicBoolean(false);

	@Before
	public void beforeWebTestBase() {
		if (started.getAndSet(true))
			return;
		TestDynamicApplication app = new TestDynamicApplication();
		FrontServlet frontServlet = new FrontServlet(app);
		String baseUrl = new StandaloneLafApplication().startForTesting(
				frontServlet, 0);
		util.initialize(baseUrl);
	}

	protected WebDriver newDriver() {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		return driver;
	}

	@Test
	public void simple() {
		WebDriver driver = newDriver();
		driver.navigate().to(url(path(SimpleMvcController.class).index()));
		assertEquals("Hello", driver.findElement(By.cssSelector("body"))
				.getText());
	}
}
