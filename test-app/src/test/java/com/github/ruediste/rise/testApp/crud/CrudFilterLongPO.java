package com.github.ruediste.rise.testApp.crud;

import java.util.Objects;

import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.test.PageObject;

public class CrudFilterLongPO extends PageObject {

    WebElement min = cached(byDataTestName("min"));
    WebElement max = cached(byDataTestName("max"));

    public CrudFilterLongPO set(long value) {
        setMin(value);
        return setMax(value);
    }

    public CrudFilterLongPO setMin(long value) {
        min.clear();
        min.sendKeys(Objects.toString(value));
        return this;
    }

    public CrudFilterLongPO setMax(long value) {
        max.clear();
        max.sendKeys(Objects.toString(value));
        return this;
    }
}
