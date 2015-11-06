package com.github.ruediste.rise.testApp.crud;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.openqa.selenium.By;

import com.github.ruediste.rise.crud.CrudControllerBase;
import com.github.ruediste.rise.test.PageObject;

public class CrudDisplayPO extends PageObject {

    @Override
    protected void initialize() {
        assertPage(CrudControllerBase.class, x -> x.display(null));
    }

    public String getPropertyText(String name) {
        return driver.findElement(By.cssSelector(
                dataTestSelector("properties") + dataTestSelector(name)))
                .getText();
    }

    public CrudBrowserPO browse() {
        driver.findElement(byDataTestName(CrudControllerBase.class,
                x -> x.browse(null, null))).click();
        return pageObject(CrudBrowserPO.class);
    }

    public CrudEditPO edit() {
        driver.findElement(
                byDataTestName(CrudControllerBase.class, x -> x.edit(null)))
                .click();
        return pageObject(CrudEditPO.class);
    }

    public CrudDeletePO delete() {
        driver.findElement(
                byDataTestName(CrudControllerBase.class, x -> x.delete(null)))
                .click();
        return pageObject(CrudDeletePO.class);
    }

    public List<String> getPropertyTestNames() {
        return driver
                .findElements(By.cssSelector(
                        dataTestSelector("properties") + "*[data-test-name]"))
                .stream().map(x -> x.getAttribute("data-test-name"))
                .collect(toList());
    }
}
