package com.github.ruediste.rise.test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class PageObjectFactory {

    public static <T extends PageObject> T createPageObject(WebDriver driver,
            Class<T> cls,
            Supplier<? extends SearchContext> rootSearchContextSupplier) {
        try {
            T result = cls.newInstance();
            result.setDriver(driver);
            result.setRootElementSupplier(rootSearchContextSupplier);
            // lookup the root context whenever the search context is requested
            SearchContext searchContext = new SearchContext() {

                @Override
                public List<WebElement> findElements(By by) {
                    return rootSearchContextSupplier.get().findElements(by);
                }

                @Override
                public WebElement findElement(By by) {
                    return rootSearchContextSupplier.get().findElement(by);
                }
            };
            PageFactory.initElements(new ElementLocatorFactory() {
                @Override
                public ElementLocator createLocator(Field field) {
                    try {
                        field.setAccessible(true);
                        if (field.get(result) != null)
                            return null;
                        else
                            return new DefaultElementLocator(searchContext,
                                    field);
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Error while determining if page object field is already set",
                                e);
                    }
                }
            }, result);
            result.initialize();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error wile instantiating page object " + cls, e);
        }
    }
}
