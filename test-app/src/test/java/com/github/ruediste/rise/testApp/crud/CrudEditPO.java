package com.github.ruediste.rise.testApp.crud;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.crud.CrudControllerBase;
import com.github.ruediste.rise.crud.DefaultCrudEditController;
import com.github.ruediste.rise.testApp.PageObject;

public class CrudEditPO extends PageObject {

    protected CrudEditPO(WebDriver driver) {
        super(driver);
        assertPage(CrudControllerBase.class, x -> x.edit(null));
    }

    public String getPropertyText(String propertyName) {
        WebElement element = getPropertyElement(propertyName);
        if ("input".equals(element.getTagName()))
            return element.getAttribute("value");
        else
            return element.getText();
    }

    public WebElement getPropertyElement(String propertyName) {
        WebElement element = driver.findElement(By
                .cssSelector(dataTestSelector("properties")
                        + dataTestSelector(propertyName)));
        return element;
    }

    public List<String> getPropertyTestNames() {
        return driver
                .findElements(
                        By.cssSelector(dataTestSelector("properties")
                                + ".form-control[data-test-name]")).stream()
                .map(x -> x.getAttribute("data-test-name")).collect(toList());
    }

    public CrudPickerPO pick(String propertyTestName) {
        clickAndWaitForRefresh(driver.findElement(By
                .cssSelector(dataTestSelector("properties")
                        + ".form-control[data-test-name=\"" + propertyTestName
                        + "\"]~* [data-test-name=\"pick\"]")));
        return new CrudPickerPO(driver);
    }

    public CrudBrowserPO browse() {
        driver.findElement(
                By.cssSelector(dataTestSelector("buttons")
                        + dataTestSelector(CrudControllerBase.class,
                                x -> x.browse(null, null)))).click();
        return new CrudBrowserPO(driver);
    }

    public CrudDisplayPO save() {

        driver.findElement(
                By.cssSelector(dataTestSelector("buttons")
                        + dataTestSelector(DefaultCrudEditController.class,
                                x -> x.save()))).click();
        return new CrudDisplayPO(driver);
    }

    public CrudEditPO setProperty(String dataTestName, String value) {
        WebElement element = getPropertyElement(dataTestName);
        element.clear();
        element.sendKeys(value);
        return this;
    }
}
