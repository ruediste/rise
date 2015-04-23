package com.github.ruediste.laf.core.entry;

import org.eclipse.jetty.server.Server;
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
		start(frontServletClass, "/*");
	}

	public void start(Class<? extends FrontServletBase> frontServletClass,
			String pathSpec) {

		try {
			ServletHolder holder = new ServletHolder(frontServletClass);
			holder.setInitOrder(0);

			ServletContextHandler ctx = new ServletContextHandler(
					ServletContextHandler.SESSIONS);
			ctx.setContextPath("");

			ctx.addServlet(holder, pathSpec);

			server = new Server(8080);
			server.setHandler(ctx);
			server.start();
			server.join();
		} catch (Exception e) {
			log.error("Error starting Jetty", e);
		}

	}

	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException("Error while stopping server", e);
		}
	}

}
