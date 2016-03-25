package com.github.ruediste.rise.testApp.crud;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.crud.CrudControllerBase;
import com.github.ruediste.rise.crud.DefaultCrudEditController;
import com.github.ruediste.rise.test.PageObject;

public class CrudEditPO extends PageObject {

    @Override
    protected void initialize() {
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
        WebElement element = driver.findElement(getPropertyElementSelector(propertyName));
        return element;
    }

    private By getPropertyElementSelector(String propertyName) {
        return By.cssSelector(dataTestSelector("properties") + dataTestSelector(propertyName));
    }

    public List<String> getPropertyTestNames() {
        return driver.findElements(By.cssSelector(dataTestSelector("properties") + ".form-control[data-test-name]"))
                .stream().map(x -> x.getAttribute("data-test-name")).collect(toList());
    }

    /**
     * Trigger the pick action on the given property
     */
    public CrudPickerPO pick(String propertyTestName) {
        clickAndWaitForRefresh(driver.findElement(By.cssSelector(dataTestSelector("properties")
                + ".form-control[data-test-name=\"" + propertyTestName + "\"]~* [data-test-name=\"pick\"]")));
        return pageObject(CrudPickerPO.class);
    }

    public CrudBrowserPO browse() {
        driver.findElement(By.cssSelector(
                dataTestSelector("buttons") + dataTestSelector(CrudControllerBase.class, x -> x.browse(null, null))))
                .click();
        return pageObject(CrudBrowserPO.class);
    }

    public CrudDisplayPO save() {

        driver.findElement(By.cssSelector(
                dataTestSelector("buttons") + dataTestSelector(DefaultCrudEditController.class, x -> x.save())))
                .click();
        return pageObject(CrudDisplayPO.class);
    }

    public CrudEditPO setProperty(String dataTestName, String value) {
        WebElement element = getPropertyElement(dataTestName);
        element.clear();
        element.sendKeys(value);
        return this;
    }

    public CrudEditBooleanPO getPropertyBoolean(String dataTestName) {
        return pageObject(CrudEditBooleanPO.class, getPropertyElementSelector(dataTestName));
    }

}
