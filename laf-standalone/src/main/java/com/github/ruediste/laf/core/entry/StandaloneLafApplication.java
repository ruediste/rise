package com.github.ruediste.laf.core.entry;

import java.nio.file.Paths;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.laf.core.front.FrontServletBase;

public class StandaloneLafApplication {

	private static final Logger log = LoggerFactory
			.getLogger(StandaloneLafApplication.class);

	private Server server;

	public void start(Class<? extends FrontServletBase> frontServletClass) {
		start(frontServletClass, 0);
	}

	public void start(Class<? extends FrontServletBase> frontServletClass,
			int port) {

		try {
			startImpl(new ServletHolder(frontServletClass), port);
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
			return startImpl(
					new ServletHolder("testFrontServlet", frontServlet), port);
		} catch (Exception e) {
			throw new RuntimeException("Error starting Jetty", e);
		}
	}

	protected String startImpl(ServletHolder holder, int port) throws Exception {
		holder.setInitOrder(0);

		ServletContextHandler ctx = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		ctx.setContextPath("");
		ctx.addServlet(holder, "/*");

		ctx.setResourceBase(Paths.get("").toString());

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
		return String.format("http://%s:%d/", host, connector.getLocalPort());
	}

	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException("Error while stopping server", e);
		}
	}

}
