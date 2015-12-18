package com.github.ruediste.rise.testApp.startupError;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.testApp.app.TestAppFrontServlet;
import com.github.ruediste.rise.testApp.app.TestRestartableApplication;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Tests an early startup error
 */
public class StartupErrorEarlyTest extends StartupErrorTest {
    private static class App extends TestRestartableApplication {
        @Override
        protected void startImpl(Injector permanentInjector) {
        }
    }

    @Override
    protected final FrontServletBase createServlet(Object testCase) {

        FrontServletBase frontServlet = new TestAppFrontServlet(App.class) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void initImpl() throws Exception {
                throw new RuntimeException("My Error");
            }
        };

        return frontServlet;
    }

    @Test
    @Ignore("needs to be run individually")
    public void test() {
        driver.navigate().to(getBaseUrl());
        assertTrue(driver.getTitle().contains("Startup Error"));
        assertFalse(driver.getPageSource().contains("My Error"));

        // make sure drop-and-create database is absent
        assertFalse(driver.getPageSource().contains("reate"));
    }

}
