package com.github.ruediste.rise.testApp.crud;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.test.PageObject;

public class CrudListPO<TSelf extends CrudListPO<TSelf>> extends PageObject {

    public TSelf search() {
        clickAndWaitForRefresh(driver.findElement(byDataTestName("search")));
        return self();
    }

    @SuppressWarnings("unchecked")
    protected TSelf self() {
        return (TSelf) this;
    }

    public TSelf setFilter(String fieldName, String value) {
        WebElement input = driver.findElement(byDataTestName(fieldName));
        input.clear();
        input.sendKeys(value);
        return self();
    }

    public CrudFilterLongPO getFilterLong(String fieldName) {
        return pageObject(CrudFilterLongPO.class, byDataTestName(fieldName));
    }

    public List<String> getColumnTestNames() {
        return driver.findElement(byDataTestName("resultList")).findElements(By.cssSelector("thead th")).stream()
                .map(e -> e.getAttribute("data-test-name")).collect(toList());
    }

    public List<String> getColumnHeadingTexts() {
        return driver.findElement(byDataTestName("resultList")).findElements(By.cssSelector("thead th")).stream()
                .map(e -> e.getText()).collect(toList());
    }

    public List<WebElement> getRows() {
        return driver.findElement(byDataTestName("resultList")).findElements(By.cssSelector("tbody tr"));
    }

    public WebElement getActions(int rowIndex) {
        return getRows().get(rowIndex).findElement(By.cssSelector("td:last-child"));
    }

}
