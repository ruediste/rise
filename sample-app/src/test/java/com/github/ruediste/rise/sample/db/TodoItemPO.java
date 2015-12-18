package com.github.ruediste.rise.sample.db;

import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.test.PageObject;

public class TodoItemPO extends PageObject {

    private WebElement name = lazy(byDataTestName("name"));
    private WebElement delete = lazy(
            byDataTestName(TodoController.class, x -> x.delete(null)));

    public String getName() {
        return name.getText();
    }

    public void delete() {
        delete.click();
    }
}
