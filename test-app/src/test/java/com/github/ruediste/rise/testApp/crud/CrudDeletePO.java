package com.github.ruediste.rise.testApp.crud;

import com.github.ruediste.rise.crud.DefaultCrudDeleteController;
import com.github.ruediste.rise.test.PageObject;

public class CrudDeletePO extends PageObject {

    public String getIdentification() {
        return driver.findElement(byDataTestName("identification")).getText();
    }

    public CrudBrowserPO delete() {
        findElement(byDataTestName(DefaultCrudDeleteController.class,
                x -> x.delete())).click();
        return pageObject(CrudBrowserPO.class);
    }
}
