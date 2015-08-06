package com.github.ruediste.rise.testApp.crud;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.crud.CrudControllerBase;

public class CrudBrowserPO extends PageObject {

    public CrudBrowserPO(WebDriver driver) {
        super(driver);
        assertPage(CrudControllerBase.class, x -> x.browse(null, null));
    }

    public void search() {
        waitForRefresh(driver.findElement(byDataTestName("search")),
                x -> x.click());
    }

    public void setFilter(String fieldName, String value) {
        WebElement input = driver.findElement(byDataTestName(fieldName));
        input.clear();
        input.sendKeys(value);
    }

    public List<String> getColumnTestNames() {
        return driver.findElement(byDataTestName("resultList"))
                .findElements(By.cssSelector("thead th")).stream()
                .map(e -> e.getAttribute("data-test-name")).collect(toList());
    }

    public List<String> getColumnHeadingTexts() {
        return driver.findElement(byDataTestName("resultList"))
                .findElements(By.cssSelector("thead th")).stream()
                .map(e -> e.getText()).collect(toList());
    }

    public List<WebElement> getRows() {
        return driver.findElement(byDataTestName("resultList")).findElements(
                By.cssSelector("tbody tr"));
    }

    /**
     * Open the view for the given row index
     */
    public CrudDisplayPO display(int rowIndex) {
        getActions(rowIndex).findElement(
                byDataTestName(CrudControllerBase.class, x -> x.display(null)))
                .click();
        return new CrudDisplayPO(driver);
    }

    public WebElement getActions(int rowIndex) {
        return getRows().get(rowIndex).findElement(
                By.cssSelector("td:last-child"));
    }

    public CrudEditPO edit(int rowIndex) {
        getActions(rowIndex).findElement(
                byDataTestName(CrudControllerBase.class, x -> x.edit(null)))
                .click();
        return new CrudEditPO(driver);
    }

    public CrudDeletePO delete(int rowIndex) {
        getActions(rowIndex).findElement(
                byDataTestName(CrudControllerBase.class, x -> x.delete(null)))
                .click();
        return new CrudDeletePO(driver);
    }
}
