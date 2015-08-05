package com.github.ruediste.rise.testApp.crud;

import org.openqa.selenium.WebDriver;

public class CrudDeletePO extends PageObject {

    protected CrudDeletePO(WebDriver driver) {
        super(driver);
    }

    public String getIdentification() {
        return driver.findElement(byDataTestName("identification")).getText();
    }

}
