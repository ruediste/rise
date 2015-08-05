package com.github.ruediste.rise.testApp.crud;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CrudEditPO extends PageObject {

    protected CrudEditPO(WebDriver driver) {
        super(driver);
    }

    public String getPropertyText(String propertyName) {
        WebElement element = driver.findElement(By
                .cssSelector(dataTestSelector("properties")
                        + dataTestSelector(propertyName)));
        if ("input".equals(element.getTagName()))
            return element.getAttribute("value");
        else
            return element.getText();
    }

}
