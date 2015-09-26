package com.github.ruediste.rise.sample.front;

import com.github.ruediste.rise.integration.StandaloneLafApplication;

public class SampleMain {

    public static void main(String[] args) throws Exception {
        StandaloneLafApplication app = new StandaloneLafApplication();
        app.start(SampleFrontServlet.class);
        app.join();
    }
}
