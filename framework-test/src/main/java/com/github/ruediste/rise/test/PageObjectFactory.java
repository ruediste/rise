package com.github.ruediste.rise.test;

import java.util.Optional;
import java.util.function.Supplier;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObjectFactory {

    public static <T extends PageObject> T createPageObject(WebDriver driver,
            Class<T> cls, Optional<Supplier<WebElement>> rootElementSupplier) {
        try {
            T result = cls.newInstance();
            result.setDriver(driver);
            result.setRootElementSupplier(rootElementSupplier);
            result.initialize();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error wile instantiating page object " + cls, e);
        }
    }
}
