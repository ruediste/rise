package com.github.ruediste.rise.sample.db;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.github.ruediste.rise.test.PageObject;

public class TodoPO extends PageObject {

    private WebElement addName;

    private WebElement addButton = lazy(
            byDataTestName(TodoController.class, x -> x.add()));

    @FindBy(css = "[data-test-name=itemList]>div")
    private List<WebElement> todos;

    public TodoPO addTodo(String name) {
        addName.clear();
        addName.sendKeys(name);
        addButton.click();
        return this;
    }

    public Optional<TodoItemPO> getTodo(String name) {
        return getTodos().stream().filter(i -> i.getName().equals(name))
                .findFirst();
    }

    public List<TodoItemPO> getTodos() {
        return todos.stream().map(e -> pageObject(TodoItemPO.class, e))
                .collect(toList());
    }
}
