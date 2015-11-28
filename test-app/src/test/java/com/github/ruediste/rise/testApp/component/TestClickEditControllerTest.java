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
        getClickEdit().click();
        doWait().untilPassing(
                () -> getClickEdit().findElement(By.cssSelector("input")));
    }

    @Test
    public void focusout_switchToViewMode() {
        getClickEdit().click();
        driver.findElement(byDataTestName("clickTarget")).click();
        doWait().untilPassing(
                () -> getClickEdit().findElement(byDataTestName("viewText")));
    }

    private WebElement getClickEdit() {
        return driver.findElement(byDataTestName(
                TestClickEditController.Data.class, x -> x.getTestLine()));
    }
}
