package com.github.ruediste.rise.test;

import static org.junit.Assert.assertNotNull;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.Servlet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.integration.StandaloneLafApplication;
import com.github.ruediste.salta.jsr330.Injector;

public abstract class WebTestBase implements TestUtil {
    @Inject
    Logger log;

    protected static class TestContainerInstance {
        public TestContainerInstance() {
        }

        boolean isStarted;
        Injector injector;

        WebDriver driver;
        boolean errorOccured;

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

    protected String url(ActionResult result) {
        Cookie sessionId = driver.manage().getCookieNamed("JSESSIONID");
        assertNotNull("No session present. Access page before using the driver",
                sessionId);
        return util.url(result, sessionId.getValue());
    }

    protected String url(PathInfo pathInfo) {
        return util.url(pathInfo);
    }

    protected <T extends IController> T go(Class<T> controllerClass) {
        return util.go(controllerClass);
    }

    private String baseUrl;

    protected WebDriver driver;

    private TestContainerInstance testContainerInstance;

    protected String getBaseUrl() {
        return baseUrl;
    }

    protected abstract TestContainerInstance getTestContainerInstance();

    @Before
    public final void beforeWebTestBase() {
        testContainerInstance = getTestContainerInstance();
        if (testContainerInstance.isStarted) {
            if (!isInjected)
                testContainerInstance.injector.injectMembers(this);
        } else {
            Servlet frontServlet = createServlet(this);

            baseUrl = new StandaloneLafApplication()
                    .startForTesting(frontServlet, 0);

            // util can be null if initialization failed
            if (util != null)
                util.initialize(baseUrl);

            testContainerInstance.injector = injector;
            testContainerInstance.isStarted = true;
            testContainerInstance.driver = createDriver();
            testContainerInstance.driver.navigate().to(url(new PathInfo("")));
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!testContainerInstance.errorOccured) {
                        testContainerInstance.driver.close();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // swallow
                        }
                    }
                }
            }));
        }
        driver = testContainerInstance.driver;
    }

    @Rule
    public final TestRule closeDriverOnSuccess() {
        return (base, description) -> new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    testContainerInstance.errorOccured = true;
                    throw t;
                }
            }
        };
    }

    /**
     * Create the servlet for the integration tests. The members of the provided
     * test case have to be injected using the restartable injector.
     */
    protected abstract Servlet createServlet(Object testCase);

    protected abstract WebDriver createDriver();

    @Override
    public WebDriver internal_getDriver() {
        return driver;
    }

}
