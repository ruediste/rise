package com.github.ruediste.rise.test;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import com.github.ruediste.remoteJUnit.client.Remote;
import com.github.ruediste.remoteJUnit.client.RemoteTestRunner;
import com.github.ruediste.remoteJUnit.codeRunner.ParentClassLoaderSupplier;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;

@RunWith(RemoteTestRunner.class)
@Remote(endpoint = "http://localhost:8080/~unitTest")
public abstract class WebTestBaseRemote implements TestUtil {
    @Inject
    Logger log;

    @Inject
    IntegrationTestUtil util;

    static class ServerParentClassLoaderSupplier implements
            ParentClassLoaderSupplier {
        private static final long serialVersionUID = 1L;

        @Override
        public ClassLoader getParentClassLoader() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    protected String url(ActionResult result) {
        Cookie sessionId = driver.manage().getCookieNamed("JSESSIONID");
        System.out.println(sessionId);
        return util
                .url(result, sessionId == null ? null : sessionId.getValue());
    }

    protected String url(PathInfo pathInfo) {
        return util.url(pathInfo);
    }

    protected <T extends IController> T go(Class<T> controllerClass) {
        return util.go(controllerClass);
    }

    protected WebDriver driver;

    protected abstract String getBaseUrl();

    @Before
    public final void beforeWebTestBase() {
        InjectorsHolder.getRestartableInjector().injectMembers(this);
        util.initialize(getBaseUrl());
    }

    @Rule
    public final TestRule closeDriverOnSuccess() {
        return (base, description) -> new Statement() {

            @Override
            public void evaluate() throws Throwable {
                driver = createDriver();
                base.evaluate();
                driver.close();
            }
        };
    }

    protected abstract WebDriver createDriver();

    @Override
    public WebDriver internal_getDriver() {
        return driver;
    }

}
