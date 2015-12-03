package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.component.components.CAutoComplete.AutoCompleteValue;
import com.github.ruediste.rise.test.elementObject.CAutoCompleteEO;
import com.github.ruediste.rise.testApp.WebTest;

public class TestClickEditControllerTest extends WebTest {

    private CAutoCompleteEO autoComplete;

    @Before
    public void before() {
        driver.navigate().to(url(go(TestClickEditController.class).index()));
        autoComplete = pageObject(CAutoCompleteEO.class,
                By.cssSelector(dataTestSelector("autoComplete") + "> input"));
    }

    @Test
    public void click_switchToEditMode() {
        getEditLine().click();
        doWait().untilPassing(() -> getEditLine().findElement(
                By.cssSelector(dataTestSelector("testLine") + "input")));
    }

    @Test
    public void focusout_switchToViewMode() {
        getEditLine().click();
        driver.findElement(byDataTestName("clickTarget")).click();
        doWait().untilPassing(() -> getEditLine().findElement(By.cssSelector(
                dataTestSelector("testLine") + dataTestSelector("viewText"))));
    }

    @Test
    public void autoComplete_text_autoSearch_push() throws Throwable {
        getAutoCompleteEditClick().click();
        Thread.sleep(1000);
        autoComplete.setText("Ruby");
        Thread.sleep(1000);
        doWait().untilPassing(() -> autoComplete.setText("Ruby"));
        pushDown();
        checkAutoCompleteValue(AutoCompleteValue
                .ofItem(new TestClickEditController.Entry("Ruby", 1)));
    }

    @Test
    public void autoComplete_search_select_push() {

    }

    private void checkAutoCompleteValue(AutoCompleteValue<?> ofItem) {
        doWait().untilPassing(() -> assertEquals(ofItem.toString(), driver
                .findElement(byDataTestName("autoCompleteValue")).getText()));
    }

    private void pushDown() {
        driver.findElement(
                byDataTestName(TestClickEditController.class, x -> x.push()))
                .click();
    }

    private WebElement getAutoCompleteEditClick() {
        return driver.findElement(byDataTestName("autoComplete"));
    }

    private WebElement getEditLine() {
        return driver.findElement(byDataTestName(
                TestClickEditController.Data.class, x -> x.getTestLine()));
    }
};