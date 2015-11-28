package com.github.ruediste.rise.testApp.component;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.test.elementObject.CAutoCompleteEO;
import com.github.ruediste.rise.testApp.WebTest;

public class TestAutoCompleteControllerTest extends WebTest {

    private CAutoCompleteEO autoComplete;

    @Before
    public void before() {
        driver.navigate().to(url(go(TestAutoCompleteController.class).index()));
        autoComplete = pageObject(CAutoCompleteEO.class,
                byDataTestName("entry"));
    }

    @Test
    public void searchUnique_noSelect_push_itemPushed() {
        autoComplete.setText("Javasc");
        driver.findElement(byDataTestName("push")).click();
        doWait().untilTrue(
                () -> driver.findElement(byDataTestName("chosenEntry"))
                        .getText().equals("JavaScript"));
    }

    @Test
    public void searchNonUnique_noSelect_push_noItemPushed() {
        autoComplete.setText("Java");
        driver.findElement(byDataTestName("push")).click();
        doWait().untilTrue(
                () -> driver.findElement(byDataTestName("chosenEntry"))
                        .getText().equals("null"));
    }

    @Test
    public void searchNonUnique_select_push_itemPushed() {
        autoComplete.setText("Java");
        doWait().untilPassing(() -> autoComplete.choose("0"));

        driver.findElement(
                byDataTestName(TestAutoCompleteController.class, x -> x.push()))
                .click();
        doWait().untilTrue(
                () -> driver.findElement(byDataTestName("chosenEntry"))
                        .getText().equals("Java"));
    }
}
