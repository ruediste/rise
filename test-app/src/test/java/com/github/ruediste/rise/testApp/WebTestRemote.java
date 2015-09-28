package com.github.ruediste.rise.testApp;

import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.integration.StandaloneLafApplication;
import com.github.ruediste.rise.test.WebTestBaseRemote;
import com.github.ruediste.rise.testApp.app.TestAppFrontServlet;
import com.github.ruediste.rise.testApp.security.LoginController;
import com.github.ruediste.rise.testApp.security.LoginPO;
import com.github.ruediste.salta.jsr330.Injector;

public class WebTestRemote extends WebTestBaseRemote {

    @Override
    protected WebDriver createDriver() {
        // return new HtmlUnitDriver(true);
        return new FirefoxDriver();
        // return new ChromeDriver();
    }

    @Override
    protected String getBaseUrl() {
        return "http://localhost:8080";
    }

    @Before
    final public void beforeWebTestRemote() {
        driver.navigate().to(url(go(LoginController.class)
                .index(new UrlSpec(new PathInfo("/")))));
        new LoginPO(driver).defaultLogin();
    }

    @Override
    protected Injector startServer() {
        StandaloneLafApplication app = new StandaloneLafApplication();
        app.start(new TestAppFrontServlet(), 8080);
        return app.getServlet().currentApplicationInfo.application
                .getRestartableInjector();
    }

}
