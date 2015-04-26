package com.github.ruediste.laf.testApp.app;

import com.github.ruediste.laf.integration.StandaloneLafApplication;

public class TestAppMain {

	public static void main(String... args) {
		new StandaloneLafApplication().start(TestAppFrontServlet.class);
	}
}
