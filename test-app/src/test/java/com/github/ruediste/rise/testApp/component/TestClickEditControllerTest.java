package com.github.ruediste.rise.testApp.component;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.testApp.WebTest;

public class TestClickEditControllerTest extends WebTest {

    @Before
    public void before() {
        driver.navigate().to(url(go(TestClickEditController.class).index()));
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
    public void autoComplete_text_autoSearch_push() {

    }

    @Test
    public void autoComplete_search_select_push() {

    }

    private WebElement getEditLine() {
        return driver.findElement(byDataTestName(
                TestClickEditController.Data.class, x -> x.getTestLine()));
    }
}
