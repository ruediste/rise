package com.github.ruediste.rise.testApp.component.generic;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.testApp.WebTest;

public class TestEditComponentsControllerTest extends WebTest {

    @Before
    public void before() {
        driver.navigate().to(url(go(TestEditComponentsController.class).index()));
    }

    @Test
    public void testEditString() {
        setString("foo");
        pushDown();
        doWait().untilPassing(() -> assertEquals("foo", stringValue()));
        setString("bar");
        doWait().untilPassing(() -> assertEquals("bar", stringComponent().getAttribute("value")));
        pullUp();
        doWait().untilPassing(() -> assertEquals("foo", stringComponent().getAttribute("value")));
    }

    private void setString(String text) {
        stringComponent().clear();
        stringComponent().sendKeys(text);
    }

    private String stringValue() {
        return values().findElement(byDataTestName("string")).getText();
    }

    private WebElement values() {
        return driver.findElement(byDataTestName("values"));
    }

    private void pushDown() {
        buttons().findElement(byDataTestName("pushDown")).click();
    }

    private void pullUp() {
        buttons().findElement(byDataTestName("pullUp")).click();
    }

    private WebElement buttons() {
        return driver.findElement(byDataTestName("buttons"));
    }

    private WebElement stringComponent() {
        return components().findElement(byDataTestName("string"));
    }

    private WebElement components() {
        return driver.findElement(byDataTestName("components"));
    }
}
