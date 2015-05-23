package com.github.ruediste.rise.sample.front;

import com.github.ruediste.rise.integration.StandaloneLafApplication;

public class SampleMain {

    public static void main(String[] args) throws Exception {
        new StandaloneLafApplication().start(SampleFrontServlet.class);
    }
}
