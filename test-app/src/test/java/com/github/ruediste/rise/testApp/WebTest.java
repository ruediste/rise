package com.github.ruediste.rise.testApp;

import javax.servlet.Servlet;

import com.github.ruediste.rise.test.WebTestBase;
import com.github.ruediste.rise.testApp.app.TestAppFrontServlet;
import com.github.ruediste.rise.testApp.app.TestDynamicApplication;
import com.github.ruediste.salta.jsr330.Injector;

public class WebTest extends WebTestBase {
	@Override
	protected final Servlet createServlet(Object testCase) {
		TestDynamicApplication app = new TestDynamicApplication() {

			@Override
			protected void startImpl(Injector permanentInjector) {
				super.startImpl(permanentInjector);
				injectMembers(testCase);
			}
		};

		Servlet frontServlet = new TestAppFrontServlet(app);

		return frontServlet;
	}

}
