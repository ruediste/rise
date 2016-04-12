package com.github.ruediste.rise.testApp.component.components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.testApp.WebTest;

public class CCheckBoxControllerTest extends WebTest {

    @Before
    public void before() {
        driver.navigate().to(url(go(CCheckBoxController.class).index()));
    }

    @Test
    public void testToggleHandler() {
        checkBox().click();
        doWait().untilPassing(() -> assertEquals("true", text().getText()));
        doWait().untilPassing(() -> assertEquals("true", checkBox().getAttribute("checked")));
        checkBox().click();
        doWait().untilPassing(() -> assertEquals("false", text().getText()));
    }

    private WebElement text() {
        return driver.findElement(byDataTestName("text"));
    }

    private WebElement checkBox() {
        return driver.findElement(byDataTestName("checkBox"));
    }
}
