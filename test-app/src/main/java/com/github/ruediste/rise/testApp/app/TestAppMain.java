package com.github.ruediste.rise.testApp.app;

import com.github.ruediste.rise.integration.StandaloneLafApplication;

public class TestAppMain {

    public static void main(String... args) {
        StandaloneLafApplication app = new StandaloneLafApplication();
        app.start(TestAppFrontServlet.class);
        app.join();
    }
}
