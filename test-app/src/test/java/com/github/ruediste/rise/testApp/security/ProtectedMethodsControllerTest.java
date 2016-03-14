package com.github.ruediste.rise.testApp.security;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.testApp.WebTest;

public class ProtectedMethodsControllerTest extends WebTest {

    @Before
    public void before() {
        driver.get(url(go(ProtectedMethodsController.class).index()));

    }

    @Test
    public void testAllowed() {
        driver.findElement(byDataTestName(ProtectedMethodsController.class, x -> x.methodAllowed())).click();
        assertPage(ProtectedMethodsController.class, x -> x.methodAllowed());
        assertEquals("success", driver.findElement(byDataTestName("data")).getText());
    }

    @Test
    public void testForbidden() {
        WebElement button = driver
                .findElement(byDataTestName(ProtectedMethodsController.class, x -> x.methodForbidden()));
        assertThat(button.getAttribute("class"), containsString("disabled"));
    }
}
