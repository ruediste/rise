package com.github.ruediste.laf.test;

import java.lang.reflect.Constructor;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.github.ruediste.laf.core.entry.ApplicationInstance;
import com.github.ruediste.laf.core.entry.FrontServletBase;
import com.google.common.reflect.TypeToken;

public abstract class ContainerTestBase<T extends ApplicationInstance> {

	protected T applicationInstance;

	protected String serverUrl;

	private Server server;

	protected WebDriver createDriver() {
		return new HtmlUnitDriver();
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setupContainerTestBase() throws Exception {

		TestFrontServlet servlet = new TestFrontServlet();

		Class<?> instanceType = TypeToken.of(getClass())
				.resolveType(ContainerTestBase.class.getTypeParameters()[0])
				.getRawType();
		try {
			Constructor<?> constructor = instanceType
					.getDeclaredConstructor(getClass());
			constructor.setAccessible(true);
			applicationInstance = (T) constructor.newInstance(this);
		} catch (NoSuchMethodException e) {
			// try other variant
			Constructor<?> constructor = instanceType.getDeclaredConstructor();
			constructor.setAccessible(true);
			applicationInstance = (T) constructor.newInstance();
		}

		servlet.setFixedApplicationInstance(applicationInstance);

		serverUrl = startForTesting(servlet);
	}

	public String startForTesting(FrontServletBase frontServlet) {
		try {
			ServletHolder holder = new ServletHolder(frontServlet);

			ServletContextHandler ctx = new ServletContextHandler(
					ServletContextHandler.SESSIONS);
			ctx.setContextPath("/");
			ctx.addServlet(holder, "/");

			server = new Server();
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(0);
			server.setConnectors(new Connector[] { connector });

			server.setHandler(ctx);
			server.start();

			String host = connector.getHost();
			if (host == null) {
				host = "localhost";
			}
			int port = connector.getLocalPort();
			return String.format("http://%s:%d/", host, port);

		} catch (Exception e) {
			throw new RuntimeException("Error starting Jetty", e);
		}
	}

	@After
	public void tearDownContainerTestBase() throws Exception {
		server.stop();
	}

}
