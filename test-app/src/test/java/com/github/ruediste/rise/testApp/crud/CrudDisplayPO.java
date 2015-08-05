package com.github.ruediste.rise.testApp.crud;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.crud.CrudControllerBase;

public class CrudDisplayPO extends PageObject {

    protected CrudDisplayPO(WebDriver driver) {
        super(driver);
        assertPage(CrudControllerBase.class, x -> x.display(null));
    }

    public String getPropertyText(String name) {
        return driver.findElement(
                By.cssSelector(dataTestSelector("properties")
                        + dataTestSelector(name))).getText();
    }

    public CrudBrowserPO browse() {
        driver.findElement(
                byDataTestName(CrudControllerBase.class,
                        x -> x.browse(null, null))).click();
        return new CrudBrowserPO(driver);
    }

    public CrudEditPO edit() {
        driver.findElement(
                byDataTestName(CrudControllerBase.class, x -> x.edit(null)))
                .click();
        return new CrudEditPO(driver);
    }

    public CrudDeletePO delete() {
        driver.findElement(
                byDataTestName(CrudControllerBase.class, x -> x.delete(null)))
                .click();
        return new CrudDeletePO(driver);
    }

    public List<String> getShownProperties() {
        return driver
                .findElements(
                        By.cssSelector(dataTestSelector("properties")
                                + "*[data-test-name]")).stream()
                .map(x -> x.getAttribute("data-test-name")).collect(toList());
    }
}