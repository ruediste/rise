package com.github.ruediste.rise.integration;

import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
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

public class RiseServer {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory
            .getLogger(RiseServer.class);

    private Server server;

    private Class<? extends FrontServletBase> frontServletClass;
    private FrontServletBase servlet;

    /**
     * Start the server on port 8080
     * 
     * @return url the server can be reached with
     */
    public String start(Class<? extends FrontServletBase> frontServletClass) {
        return start(frontServletClass, 8080);
    }

    /**
     * Start the server on the given port
     * 
     * @return Url the server can be reached with
     */
    public String start(Class<? extends FrontServletBase> frontServletClass,
            int port) {
        this.frontServletClass = frontServletClass;
        if (Modifier.isAbstract(frontServletClass.getModifiers()))
            throw new RuntimeException(
                    "Front servlet class may not be abstact: "
                            + frontServletClass.getName());
        try {
            return startImpl(new ServletHolder(frontServletClass), port);
        } catch (Exception e) {
            throw new RuntimeException("Error starting Jetty", e);
        }
    }

    /**
     * @param frontServlet
     * @param port
     *            port to start server on, 0 for random port
     */
    public String start(FrontServletBase frontServlet, int port) {
        frontServletClass = frontServlet.getClass();
        try {
            return startImpl(
                    new ServletHolder("testFrontServlet", frontServlet), port);
        } catch (Exception e) {
            throw new RuntimeException("Error starting Jetty", e);
        }
    }

    /**
     * Wait until the server is {@link #stop() stopped}.
     */
    public void join() {
        try {
            server.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected String startImpl(ServletHolder holder, int port)
            throws Exception {
        holder.setInitOrder(0);

        {
            MultipartConfig multipartConfig = frontServletClass
                    .getAnnotation(MultipartConfig.class);
            if (multipartConfig != null)

                holder.getRegistration().setMultipartConfig(
                        new MultipartConfigElement(multipartConfig));
            else
                holder.getRegistration()
                        .setMultipartConfig(new MultipartConfigElement(""));
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

        this.servlet = (FrontServletBase) holder.getServlet();

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

    public FrontServletBase getServlet() {
        return servlet;
    }

    public Class<? extends FrontServletBase> getFrontServletClass() {
        return frontServletClass;
    }

}
