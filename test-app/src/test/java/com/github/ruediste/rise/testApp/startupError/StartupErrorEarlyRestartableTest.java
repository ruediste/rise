package com.github.ruediste.rise.testApp.startupError;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.Servlet;

import org.junit.Test;
import org.openqa.selenium.By;

import com.github.ruediste.rise.testApp.app.TestAppFrontServlet;
import com.github.ruediste.rise.testApp.app.TestRestartableApplication;
import com.github.ruediste.salta.jsr330.Injector;

public class StartupErrorEarlyRestartableTest extends StartupErrorTest {
    @Override
    protected final Servlet createServlet(Object testCase) {
        TestRestartableApplication app = new TestRestartableApplication() {

            @Override
            protected void startImpl(Injector permanentInjector) {
                throw new RuntimeException("My Error");
            }
        };

        Servlet frontServlet = new TestAppFrontServlet(app) {
            private static final long serialVersionUID = 1L;
        };

        return frontServlet;
    }

    @Test
    public void test() {
        driver.navigate().to(getBaseUrl());
        assertTrue(driver.getTitle().contains("Startup Error"));
        assertTrue(driver.getPageSource().contains("My Error"));

        // make sure drop-and-create database is present
        assertNotNull(driver.findElement(By.cssSelector("input")));
    }

}
