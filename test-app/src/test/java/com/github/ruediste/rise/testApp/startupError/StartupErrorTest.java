package com.github.ruediste.rise.testApp.startupError;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.github.ruediste.rise.test.WebTestBase;

public abstract class StartupErrorTest extends WebTestBase {
    private TestContainerInstance container = new TestContainerInstance();

    @Override
    protected WebDriver createDriver() {
        // HtmlUnitDriver driver = new HtmlUnitDriver(true);
        FirefoxDriver driver = new FirefoxDriver();
        return driver;
    }

    @Override
    protected TestContainerInstance getTestContainerInstance() {
        return container;
    }
}
