package com.github.ruediste.rise.sample.test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.github.ruediste.rise.integration.RiseServer;
import com.github.ruediste.rise.sample.front.SampleFrontServlet;
import com.github.ruediste.rise.sample.welcome.WelcomeController;
import com.github.ruediste.rise.test.WebTestBase;

public class WebTest extends WebTestBase {

    @Override
    protected String getBaseUrl() {
        return "http://localhost:8080";
    }

    @Override
    protected RiseServer startServer() {
        RiseServer server = new RiseServer();
        server.start(new SampleFrontServlet(true), 8080);
        return server;
    }

    @Override
    protected WebDriver createDriver() {
        return new ChromeDriver();
    }

    protected void startSession() {
        driver.navigate().to(url(go(WelcomeController.class).index()));
    }
}
