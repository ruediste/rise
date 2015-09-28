package com.github.ruediste.rise.sample.front;

import com.github.ruediste.rise.integration.RiseServer;

public class SampleMain {

    public static void main(String[] args) throws Exception {
        RiseServer app = new RiseServer();
        app.start(SampleFrontServlet.class);
        app.join();
    }
}
