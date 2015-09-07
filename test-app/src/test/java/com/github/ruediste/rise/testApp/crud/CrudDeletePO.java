package com.github.ruediste.rise.testApp.crud;

import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.crud.DefaultCrudDeleteController;
import com.github.ruediste.rise.testApp.PageObject;

public class CrudDeletePO extends PageObject {

    protected CrudDeletePO(WebDriver driver) {
        super(driver);
    }

    public String getIdentification() {
        return driver.findElement(byDataTestName("identification")).getText();
    }

    public CrudBrowserPO delete() {
        driver.findElement(
                byDataTestName(DefaultCrudDeleteController.class,
                        x -> x.delete())).click();
        return new CrudBrowserPO(driver);
    }
}
