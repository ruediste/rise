package com.github.ruediste.rise.sample.welcome;

import org.junit.Test;

import com.github.ruediste.rise.sample.test.WebTest;

public class WelcomeControllerTest extends WebTest {

    @Test
    public void testWelcomPageReachable() {
        driver.navigate().to(url(go(WelcomeController.class).index()));
        assertPage(WelcomeController.class, x -> x.index());
    }
}
