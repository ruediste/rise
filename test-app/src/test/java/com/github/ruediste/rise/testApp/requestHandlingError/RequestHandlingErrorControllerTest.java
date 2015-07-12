package com.github.ruediste.rise.testApp.requestHandlingError;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.ruediste.rise.testApp.WebTest;

public class RequestHandlingErrorControllerTest extends WebTest {

    @Test
    public void test() {
        driver.navigate().to(
                url(go(RequestHandlingErrorController.class).index()));
        assertThat(driver.getPageSource(), containsString("Boom!"));
    }
}
