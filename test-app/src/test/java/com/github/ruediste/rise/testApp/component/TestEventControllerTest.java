package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.testApp.WebTest;
import com.google.common.base.Predicate;

public class TestEventControllerTest extends WebTest {

    @Test
    public void testEventTriggering() {
        driver.navigate().to(url(go(TestEventController.class).index()));
        assertEquals("0", getEventCount());
        driver.findElement(byDataTestName("eventSpan")).click();
        doWait().until(new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver input) {
                return "1".equals(getEventCount());
            }
        });
    }

    private String getEventCount() {
        return driver.findElement(byDataTestName("eventCount")).getText();
    }
}
