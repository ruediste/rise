package com.github.ruediste.rise.test;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.Servlet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.integration.StandaloneLafApplication;
import com.github.ruediste.rise.mvc.IControllerMvc;
import com.github.ruediste.salta.jsr330.Injector;

public abstract class WebTestBase {
    @Inject
    Logger log;

    protected static class TestContainerInstance {
        public TestContainerInstance() {
        }

        boolean isStarted;
        Injector injector;
    }

    @Inject
    IntegrationTestUtil util;

    @Inject
    Injector injector;

    private boolean isInjected;

    @PostConstruct
    public void setInjected() {
        isInjected = true;
    }

    protected WebDriver driver;

    protected String url(ActionResult result) {
        return util.url(result);
    }

    protected String url(PathInfo pathInfo) {
        return util.url(pathInfo);
    }

    protected <T extends IControllerMvc> T go(Class<T> controllerClass) {
        return util.go(controllerClass);
    }

    private String baseUrl;

    protected String getBaseUrl() {
        return baseUrl;
    }

    protected abstract TestContainerInstance getTestContainerInstance();

    @Before
    public void beforeWebTestBase() {
        TestContainerInstance testContainerInstance = getTestContainerInstance();
        if (testContainerInstance.isStarted) {
            if (!isInjected)
                testContainerInstance.injector.injectMembers(this);
        } else {
            Servlet frontServlet = createServlet(this);

            baseUrl = new StandaloneLafApplication().startForTesting(
                    frontServlet, 0);

            // util can be null if initialization failed
            if (util != null)
                util.initialize(baseUrl);

            testContainerInstance.injector = injector;
            testContainerInstance.isStarted = true;
        }
        driver = createDriver();
    }

    @Rule
    public final TestRule closeDriverOnSuccess() {
        return (base, description) -> new Statement() {

            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                try {
                    driver.close();
                } catch (Throwable t) {
                    // swallow
                }
            }
        };
    }

    /**
     * Create the servlet for the integration tests
     */
    protected abstract Servlet createServlet(Object testCase);

    protected abstract WebDriver createDriver();

}
