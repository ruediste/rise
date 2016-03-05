package com.github.ruediste.rise.testApp.crud;

import org.openqa.selenium.By;

import com.github.ruediste.rise.test.PageObject;

public class ActionMethodInvocationResultPO extends PageObject {

    public String getPropertyText(String name) {
        return findElement(By.cssSelector(dataTestSelector("properties") + dataTestSelector(name))).getText();
    }

    public void ok() {
        findElement(byDataTestName("ok")).click();
    }
}
