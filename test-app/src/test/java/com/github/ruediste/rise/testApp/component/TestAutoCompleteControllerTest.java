package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.component.components.CAutoComplete.AutoSearchMode;
import com.github.ruediste.rise.test.elementObject.AutoCompleteEO;
import com.github.ruediste.rise.testApp.WebTest;

public class TestAutoCompleteControllerTest extends WebTest {

    private AutoCompleteEO autoComplete;

    @Before
    public void before() {
        autoComplete = pageObject(AutoCompleteEO.class,
                byDataTestName("entry"));
    }

    private void open(AutoSearchMode mode) {
        driver.navigate()
                .to(url(go(TestAutoCompleteController.class).index(mode)));
    }

    @Test
    public void text_enter_reloadCorrect() {
        open(AutoSearchMode.SINGLE);
        autoComplete.setText("foo").enter();
        doWait().untilPassing(() -> {
            assertEquals("foo", autoComplete.getText());
        });
    }

    @Test
    public void searchUnique_noSelect_push_itemPushed() {
        open(AutoSearchMode.SINGLE);
        autoComplete.setText("Javasc");
        push();
        checkControllerState("JavaScript");
    }

    @Test
    public void searchNonUnique_noSelect_push_noItemPushed() {
        open(AutoSearchMode.SINGLE);
        autoComplete.setText("Java");
        push();
        checkControllerState("null");
    }

    @Test
    public void searchNonUnique_select_push_itemPushed() {
        open(AutoSearchMode.SINGLE);
        autoComplete.setText("Java");
        doWait().untilPassing(() -> autoComplete.choose("0"));
        push();
        checkControllerState("Java");
    }

    private void checkControllerState(String expected) {
        doWait().untilTrue(() -> {
            return driver.findElement(byDataTestName("chosenEntry")).getText()
                    .equals(expected);
        });
    }

    private void push() {
        driver.findElement(
                byDataTestName(TestAutoCompleteController.class, x -> x.push()))
                .click();
    }
}
