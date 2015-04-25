package com.github.ruediste.laf.test;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.github.ruediste.laf.core.front.DynamicApplication;
import com.github.ruediste.laf.core.front.DynamicApplicationBase;
import com.github.ruediste.laf.core.front.FrontServletBase;
import com.github.ruediste.laf.integration.DynamicIntegrationModule;
import com.github.ruediste.laf.integration.PermanentIntegrationModule;
import com.github.ruediste.laf.integration.StandaloneLafApplication;
import com.github.ruediste.laf.mvc.InvocationActionResult;
import com.github.ruediste.laf.mvc.web.ActionInvocationUtil;
import com.github.ruediste.laf.mvc.web.MvcWebRenderUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class SimpleMvcControllerTest {

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

	class TestApp extends DynamicApplicationBase {

		@Override
		protected void startImpl(Injector permanentInjector) {
			Injector injector = Salta
					.createInjector(new DynamicIntegrationModule(
							permanentInjector));
			injector.injectMembers(this);
			injector.injectMembers(SimpleMvcControllerTest.this);
		}

	}

	@Inject
	MvcWebRenderUtil util;

	@Inject
	ActionInvocationUtil actionInvocationUtil;

	@Test
	public void simple() {
		TestApp app = new TestApp();
		FrontServlet frontServlet = new FrontServlet(app);
		String url = new StandaloneLafApplication().startForTesting(
				frontServlet, 0);
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.navigate().to(url);
		driver.navigate().to(
				url
						+ actionInvocationUtil.toPathInfo(
								(InvocationActionResult) util.path(
										SimpleMvcController.class).index())
								.getValue());
		assertEquals("Hello", driver.findElement(By.cssSelector("body"))
				.getText());
	}
}
