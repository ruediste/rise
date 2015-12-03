package com.github.ruediste.rise.test;

import org.openqa.selenium.WebDriver;

public class TestUtilImpl implements TestUtil {

    private WebDriver driver;

    public TestUtilImpl() {

    }

    public TestUtilImpl(WebDriver driver) {
        this.setDriver(driver);
    }

    @Override
    public WebDriver internal_getDriver() {
        return getDriver();
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

}
