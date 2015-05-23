package com.github.ruediste.rise.testApp.app;

import com.github.ruediste.rise.integration.StandaloneLafApplication;

public class TestAppMain {

    public static void main(String... args) {
        new StandaloneLafApplication().start(TestAppFrontServlet.class);
    }
}
