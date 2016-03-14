package com.github.ruediste.rise.testApp.startupError;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.testApp.app.TestAppFrontServlet;
import com.github.ruediste.rise.testApp.app.TestRestartableApplication;
import com.github.ruediste.salta.jsr330.Injector;

public class StartupErrorRestartableAfterInjectionTest extends StartupErrorTest {
    private static class App extends TestRestartableApplication {
        @Override
        protected void startImpl(Injector nonRestartableInjector) {
            super.startImpl(nonRestartableInjector);
            throw new RuntimeException("My Error");
        }
    }

    @Override
    protected final FrontServletBase createServlet(Object testCase) {
        FrontServletBase frontServlet = new TestAppFrontServlet(App.class) {
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
