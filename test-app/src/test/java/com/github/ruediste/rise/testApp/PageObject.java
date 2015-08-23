package com.github.ruediste.rise.testApp;

import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.test.TestUtil;

public class PageObject implements TestUtil {

    protected final WebDriver driver;

    protected PageObject(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public WebDriver internal_getDriver() {
        return driver;
    }

}
