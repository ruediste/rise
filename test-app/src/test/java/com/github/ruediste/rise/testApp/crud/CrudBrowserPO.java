package com.github.ruediste.rise.testApp.crud;

import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.crud.CrudControllerBase;

public class CrudBrowserPO extends PageObject {

    protected CrudBrowserPO(WebDriver driver) {
        super(driver);
        assertPage(CrudControllerBase.class, x -> x.browse(null, null));
    }

    public void search() {
        driver.findElement(byDataTestName("search")).click();
    }
}
