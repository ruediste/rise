package com.github.ruediste.rise.test;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import com.github.ruediste.remoteJUnit.client.Remote;
import com.github.ruediste.remoteJUnit.client.RemoteTestRunner;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.RemotUnitTestInitializer;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.integration.RiseServer;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder.Holder;
import com.github.ruediste.salta.jsr330.Injector;

@RunWith(RemoteTestRunner.class)
@Remote(endpoint = "http://localhost:8080/~unitTest")
public abstract class WebTestBase implements TestUtil {
    @Inject
    Logger log;

    @Inject
    IntegrationTestUtil util;

    protected String url(ActionResult result) {
        Cookie sessionId = driver.manage().getCookieNamed("JSESSIONID");
        return util.url(result, sessionId == null ? () -> {
            throw new RuntimeException("no session present");
        } : () -> sessionId.getValue());
    }

    protected String url(PathInfo pathInfo) {
        return util.url(pathInfo);
    }

    protected <T extends IController> T go(Class<T> controllerClass) {
        return util.go(controllerClass);
    }

    protected WebDriver driver;

    /**
     * Get the url the application is reachable under
     */
    protected abstract String getBaseUrl();

    private static Boolean isRunningLocally;
    private static final Object lock = new Object();
    private static Injector injector;
    private static RiseServer server;

    /**
     * If {@link #runWithSeparateServer()} returns true, this variable is
     * initialized to the current test case. If this variable is not null the
     * server was created for a specific test instance.
     */
    private static Object serverTestCase;

    /**
     * If true is returned, the server is restarted for this test class. Only
     * makes sense when annotating the test case with
     * {@link Remote#endpoint() @Remote(endpoint="-")}
     */
    protected boolean runWithSeparateServer() {
        return false;
    }

    /**
     * In case the remote server is not reachable, start the server and return
     * it
     */
    protected abstract RiseServer startServer();

    @Rule
    public final TestRule closeDriverOnSuccess() {
        return (base, description) -> new Statement() {

            @Override
            public void evaluate() throws Throwable {
                driver = createDriver();
                try {
                    base.evaluate();
                } finally {
                    try {
                        driver.close();
                    } catch (Throwable t) {
                        // swallow
                    }
                }
            }
        };
    }

    /**
     * If the test is executed remotely, the {@link RemotUnitTestInitializer}
     * will have initialized the {@link InjectorsHolder}. Otherwise we start a
     * server locally and run the tests locally, too.
     */
    @Rule
    public final TestRule setupServer() {
        boolean runWithSeparateServer = runWithSeparateServer();

        synchronized (lock) {
            // stop the server if necessary
            if (
            // server was started for a different test with separate server
            (serverTestCase != null && serverTestCase != this) ||
                    // request separate test case
                    (runWithSeparateServer && serverTestCase != this)) {
                if (server != null)
                    server.stop();
                server = null;
                isRunningLocally = null;
            }

            if (isRunningLocally == null) {
                if (InjectorsHolder.injectorsPresent()) {
                    // we are running remotely
                    injector = InjectorsHolder.getRestartableInjector();
                    isRunningLocally = false;
                    if (runWithSeparateServer)
                        throw new RuntimeException(
                                "Cannot run server remotely when runWithSeparateServer() returns true");
                } else {
                    server = startServer();

                    injector = server.getServlet()
                            .getCurrentRestartableInjector();
                    isRunningLocally = true;
                    if (runWithSeparateServer)
                        serverTestCase = this;
                    else
                        serverTestCase = null;
                }
            }
        }

        if (util == null && injector != null) {
            injector.injectMembers(this);
            util.initialize(getBaseUrl());
        }

        if (Boolean.TRUE.equals(isRunningLocally)) {
            // setup the InjectorsHolder when running locally
            return (base, description) -> new Statement() {

                @Override
                public void evaluate() throws Throwable {
                    Holder holder = InjectorsHolder.setInjectors(
                            server.getServlet().getNonRestartableInjector(),
                            server.getServlet()
                                    .getCurrentRestartableInjector());
                    try {
                        base.evaluate();
                    } finally {
                        InjectorsHolder.restoreInjectors(holder);
                    }
                }
            };
        } else
            return (base, description) -> base;
    }

    protected abstract WebDriver createDriver();

    @Override
    public WebDriver internal_getDriver() {
        return driver;
    }
}
