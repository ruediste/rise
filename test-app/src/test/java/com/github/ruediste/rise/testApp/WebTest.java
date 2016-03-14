package com.github.ruediste.rise.testApp;

import org.junit.Before;
import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.integration.RiseServer;
import com.github.ruediste.rise.test.WebTestBase;
import com.github.ruediste.rise.testApp.app.TestAppFrontServlet;
import com.github.ruediste.rise.testApp.app.TestRestartableApplication;
import com.github.ruediste.rise.testApp.security.LoginController;

public abstract class WebTest extends WebTestBase {

    @Override
    protected WebDriver createDriver() {
        return WebDriverFactory.createDriver();
    }

    @Override
    protected String getBaseUrl() {
        return "http://localhost:8080";
    }

    protected void startSession() {
        driver.navigate().to(url(go(LoginController.class).index(new UrlSpec(new PathInfo("/")))));
    }

    @Before
    final public void beforeWebTest() {
        // startSession();
        // pageObject(LoginPO.class).defaultLogin();
    }

    @Override
    protected RiseServer startServer() {
        RiseServer server = new RiseServer();
        server.start(new TestAppFrontServlet(TestRestartableApplication.class), 8080);
        return server;
    }

}
