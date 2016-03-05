package com.github.ruediste.rise.testApp.crud;

import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.test.PageObject;

public class ActionMethodInvocationPO extends PageObject {

    public String getArgumentText(String propertyName) {
        WebElement element = getArgumentElement(propertyName);
        if ("input".equals(element.getTagName()))
            return element.getAttribute("value");
        else
            return element.getText();
    }

    public WebElement getArgumentElement(String argumentName) {

        WebElement element = driver.findElement(byDataTestName("properties")).findElement(byDataTestName(argumentName));
        return element;
    }

    public ActionMethodInvocationPO setArgument(String dataTestName, String value) {
        WebElement element = getArgumentElement(dataTestName);
        element.clear();
        element.sendKeys(value);
        return this;
    }

    public ActionMethodInvocationResultPO invoke() {
        findElement(byDataTestName("invoke")).click();
        return pageObject(ActionMethodInvocationResultPO.class);
    }

    public void cancel() {
        findElement(byDataTestName("cancel")).click();
    }

    public ActionMethodInvocationResultPO noArgs() {
        return pageObject(ActionMethodInvocationResultPO.class);
    }
}
