package com.github.ruediste.rise.test;

import java.util.Optional;
import java.util.function.Supplier;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObject implements TestUtil {

    protected WebDriver driver;
    private Optional<Supplier<WebElement>> rootElementSupplier;

    /**
     * Called after fields have been initialized. No need to call super()
     */
    protected void initialize() {

    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public Optional<Supplier<WebElement>> getRootElementSupplier() {
        return rootElementSupplier;
    }

    public WebElement rootElement() {
        return getRootElementSupplier()
                .orElseThrow(
                        () -> new RuntimeException("No root element defined"))
                .get();
    }

    public void setRootElementSupplier(
            Optional<Supplier<WebElement>> rootElementSupplier) {
        this.rootElementSupplier = rootElementSupplier;
    }

    @Override
    public WebDriver internal_getDriver() {
        return driver;
    }
}
