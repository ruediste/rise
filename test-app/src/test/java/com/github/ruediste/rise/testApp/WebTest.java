package com.github.ruediste.rise.testApp;

import javax.servlet.Servlet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.github.ruediste.rise.test.WebTestBase;
import com.github.ruediste.rise.testApp.app.TestAppFrontServlet;
import com.github.ruediste.rise.testApp.app.TestRestartableApplication;
import com.github.ruediste.salta.jsr330.Injector;

public class WebTest extends WebTestBase {

    private static TestContainerInstance containerInstance = new TestContainerInstance();

    @Override
    protected TestContainerInstance getTestContainerInstance() {
        return containerInstance;
    }

    @Override
    protected final Servlet createServlet(Object testCase) {
        TestRestartableApplication app = new TestRestartableApplication() {

            @Override
            protected void startImpl(Injector permanentInjector) {
                super.startImpl(permanentInjector);
                injectMembers(testCase);
            }
        };

        Servlet frontServlet = new TestAppFrontServlet(app);

        return frontServlet;
    }

    @Override
    protected WebDriver createDriver() {
        // HtmlUnitDriver driver = new HtmlUnitDriver(true);
        FirefoxDriver driver = new FirefoxDriver();
        return driver;
    }

}
