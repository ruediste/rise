package com.github.ruediste.rise.testApp.startupError;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.github.ruediste.remoteJUnit.client.Remote;
import com.github.ruediste.rise.integration.RiseServer;
import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.test.WebTestBase;

@Remote(endpoint = "-")
public abstract class StartupErrorTest extends WebTestBase {

    @Override
    protected WebDriver createDriver() {
        // HtmlUnitDriver driver = new HtmlUnitDriver(true);
        FirefoxDriver driver = new FirefoxDriver();
        return driver;
    }

    @Override
    protected RiseServer startServer() {
        RiseServer server = new RiseServer();
        server.start(createServlet(this), 8080);
        return server;
    }

    @Override
    protected String getBaseUrl() {
        return "http://localhost:8080";
    }

    @Override
    protected boolean shouldRestartServer() {
        return true;
    }

    protected abstract FrontServletBase createServlet(Object testCase);
}
