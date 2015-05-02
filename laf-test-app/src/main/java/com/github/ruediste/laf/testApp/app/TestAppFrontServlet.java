package com.github.ruediste.laf.testApp.app;

import com.github.ruediste.laf.core.front.FrontServletBase;

public abstract class TestAppFrontServlet extends FrontServletBase {
	private static final long serialVersionUID = 1L;

	public TestAppFrontServlet(TestDynamicApplication fixedApplicationInstance) {
		super(fixedApplicationInstance);
	}

	public TestAppFrontServlet() {
		super(TestDynamicApplication.class);
	}

}