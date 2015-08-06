package com.github.ruediste.rise.testApp.crud;

import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.crud.DefaultCrudDeleteController;

public class CrudDeletePO extends PageObject {

    protected CrudDeletePO(WebDriver driver) {
        super(driver);
    }

    public String getIdentification() {
        return driver.findElement(byDataTestName("identification")).getText();
    }

    public CrudBrowserPO delete() {
        clickAndWaitForRefresh(driver.findElement(byDataTestName(
                DefaultCrudDeleteController.class, x -> x.delete())));
        return new CrudBrowserPO(driver);
    }
}
