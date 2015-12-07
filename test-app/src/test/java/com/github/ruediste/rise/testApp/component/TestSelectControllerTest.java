package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.github.ruediste.rise.testApp.WebTest;

public class TestSelectControllerTest extends WebTest {

    private Actions actions;

    @Before
    public void before() {
        actions = new Actions(driver);
    }

    private void open(boolean allowEmpty, String initialSelection,
            boolean addHandler) {
        driver.navigate().to(url(go(TestSelectController.class)
                .index(allowEmpty, initialSelection, addHandler)));
    }

    @Test
    public void noneSelected_selectionCorrect() {
        open(true, null, false);
        checkSelection(Optional.empty());
    }

    @Test
    public void selected_selectionCorrect() {
        open(true, "bar", false);
        checkSelection(Optional.of("bar"));
    }

    @Test
    public void noneSelected_select_push_pull() {
        open(true, null, false);
        select("foo");
        pushDown();
        pullUp();
        checkSelection(Optional.of("foo"));
    }

    @Test
    public void noneSelected_select_push() {
        open(true, null, false);
        select("foo");
        pushDown();
        checkState(Optional.of("foo"));
    }

    @Test
    public void withSelectHandler_changeSelection_viewStatusUpdated() {
        open(true, null, true);
        checkViewStatus(Optional.empty());
        select("foo");
        checkViewStatus(Optional.of("foo"));
    }

    @Test
    public void noSelectHandler_changeSelection_viewStatusNotUpdated() {
        open(true, null, false);
        checkViewStatus(Optional.empty());
        select("foo");
        checkViewStatus(Optional.empty());
    }

    private void checkViewStatus(Optional<Object> item) {
        doWait().untilPassing(() -> assertEquals(String.valueOf(item),
                driver.findElement(byDataTestName("viewStatus")).getText()));
    }

    private void checkSelection(Optional<String> of) {
        String selection = driver
                .findElement(By.cssSelector(
                        dataTestSelector("selectedItem") + "*[selected]"))
                .getAttribute("data-test-name");
        Optional<String> current;
        if ("-".equals(selection))
            current = Optional.empty();
        else
            current = Optional.of(selection);
        assertEquals(of, current);
    }

    private void select(String item) {
        driver.findElement(byDataTestName(item)).click();
    }

    private void checkState(Optional<String> expected) {
        doWait().untilPassing(() -> assertEquals(expected.toString(), driver
                .findElement(byDataTestName("controllerStatus")).getText()));

    }

    private void pushDown() {
        driver.findElement(
                byDataTestName(TestSelectController.class, x -> x.pushDown()))
                .click();
    }

    private void pullUp() {
        doWait().untilPassing(() -> driver.findElement(
                byDataTestName(TestSelectController.class, x -> x.pullUp()))
                .click());
    }
}
