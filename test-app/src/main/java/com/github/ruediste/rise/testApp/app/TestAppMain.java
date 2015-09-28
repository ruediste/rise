package com.github.ruediste.rise.testApp.app;

import com.github.ruediste.rise.integration.RiseServer;

public class TestAppMain {

    public static void main(String... args) {
        RiseServer app = new RiseServer();
        app.start(TestAppFrontServlet.class);
        app.join();
    }
}
