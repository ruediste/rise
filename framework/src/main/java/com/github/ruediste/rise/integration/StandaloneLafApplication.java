package com.github.ruediste.rise.integration;

import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.annotation.MultipartConfig;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.GzipFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;

public class StandaloneLafApplication {

    private static final Logger log = LoggerFactory
            .getLogger(StandaloneLafApplication.class);

    private Server server;

    /**
     * Start the server on port 8080
     */
    public void start(Class<? extends FrontServletBase> frontServletClass) {

        start(frontServletClass, 8080);
    }

    public void start(Class<? extends FrontServletBase> frontServletClass,
            int port) {
        if (Modifier.isAbstract(frontServletClass.getModifiers()))
            throw new RuntimeException(
                    "Front servlet class may not be abstact: "
                            + frontServletClass.getName());
        try {
            startImpl(frontServletClass, new ServletHolder(frontServletClass),
                    port);
            server.join();
        } catch (Exception e) {
            log.error("Error starting Jetty", e);
        }
    }

    /**
     * @param port
     *            port to start server on, 0 for random port
     * @param frontServlet
     */
    public String startForTesting(Servlet frontServlet, int port) {

        try {
            return startImpl(frontServlet.getClass(), new ServletHolder(
                    "testFrontServlet", frontServlet), port);
        } catch (Exception e) {
            throw new RuntimeException("Error starting Jetty", e);
        }
    }

    protected String startImpl(Class<?> frontServletClass,
            ServletHolder holder, int port) throws Exception {
        holder.setInitOrder(0);

        {
            MultipartConfig multipartConfig = frontServletClass
                    .getAnnotation(MultipartConfig.class);
            if (multipartConfig != null)

                holder.getRegistration().setMultipartConfig(
                        new MultipartConfigElement(multipartConfig));
            else
                holder.getRegistration().setMultipartConfig(
                        new MultipartConfigElement(""));
        }

        ServletContextHandler ctx = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        ctx.setContextPath("");
        ctx.addServlet(holder, "/*");

        ctx.setResourceBase(Paths.get("").toString());
        ctx.addFilter(GzipFilter.class, "/*",
                EnumSet.of(DispatcherType.REQUEST));

        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });

        server.setHandler(ctx);
        server.start();

        String host = connector.getHost();
        if (host == null) {
            host = "localhost";
        }
        return String.format("http://%s:%d", host, connector.getLocalPort());
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException("Error while stopping server", e);
        }
    }

}
