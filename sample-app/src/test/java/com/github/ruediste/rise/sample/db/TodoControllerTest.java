package com.github.ruediste.rise.sample.db;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.sample.test.WebTest;

public class TodoControllerTest extends WebTest {

    TodoPO po;

    @Before
    public void before() {
        // start a session. enables the generation of signed urls
        startSession();
        driver.navigate().to(url(go(TodoController.class).index()));
        po = pageObject(TodoPO.class);
    }

    @Test
    public void testAddTodo() {
        String name = RandomStringUtils.randomAlphanumeric(10);
        po.addTodo(name);
        checkTodoItemPresent(name);
    }

    @Test
    public void testAddAndRemoveTodo() {
        String name = RandomStringUtils.randomAlphanumeric(10);
        po.addTodo(name);

        checkTodoItemPresent(name);
        po.getTodo(name).get().delete();
        checkTodoItemNotPresent(name);
    }

    private void checkTodoItemPresent(String name) {
        doWait().untilPassing(() -> assertThat(
                po.getTodos().stream().map(x -> x.getName()).collect(toList()),
                hasItem(name)));
    }

    private void checkTodoItemNotPresent(String name) {
        doWait().untilPassing(() -> assertThat(
                po.getTodos().stream().map(x -> x.getName()).collect(toList()),
                not(hasItem(name))));
    }
}
