package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.ruediste.rise.testApp.WebTest;

public class TestEventControllerTest extends WebTest {

    @Test
    public void testEventTriggering() {
        driver.navigate().to(url(go(TestEventController.class).index()));
        assertEquals("0", getEventCount());
        driver.findElement(byDataTestName("eventSpan")).click();
        doWait().untilTrue(() -> "1".equals(getEventCount()));
    }

    private String getEventCount() {
        return driver.findElement(byDataTestName("eventCount")).getText();
    }
}
