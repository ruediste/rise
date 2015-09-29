package com.github.ruediste.rise.sample.front;

import org.elasticsearch.node.NodeBuilder;

import com.github.ruediste.rise.integration.RiseServer;

public class SampleMain {

    public static void main(String[] args) throws Exception {

        NodeBuilder builder = NodeBuilder.nodeBuilder().local(true);
        builder.settings().put("path.home", "./elastic");
        builder.build();

        RiseServer app = new RiseServer();
        app.start(SampleFrontServlet.class);
        app.join();
    }
}
