package com.github.ruediste.laf.testApp.app;

import com.github.ruediste.laf.core.front.DynamicApplication;
import com.github.ruediste.laf.core.front.FrontServletBase;
import com.github.ruediste.laf.integration.PermanentIntegrationModule;
import com.github.ruediste.salta.jsr330.Salta;

public class TestAppFrontServlet extends FrontServletBase {
	private static final long serialVersionUID = 1L;

	public TestAppFrontServlet(DynamicApplication fixedApplicationInstance) {
		super(fixedApplicationInstance);
	}

	public TestAppFrontServlet() {
		super(TestDynamicApplication.class);
	}

	@Override
	protected void initImpl() throws Exception {
		Salta.createInjector(new PermanentIntegrationModule(getServletConfig()))
				.injectMembers(this);
	}

}