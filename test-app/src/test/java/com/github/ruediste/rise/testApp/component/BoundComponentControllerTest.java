package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.openqa.selenium.By;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.testApp.WebTest;

public class BoundComponentControllerTest extends WebTest {
    @Inject
    ComponentUtil util;

    @Test
    public void test() {
        driver.navigate().to(url(util.go(BoundComponentController.class).index()));
        check("", "");
        driver.findElement(byDataTestName("value")).sendKeys("foo");
        driver.findElement(By.cssSelector(".pushDown")).click();
        check("", "foo");
        driver.findElement(By.cssSelector(".pullUp")).click();
        check("foo", "foo");
    }

    void check(String bound, String direct) {
        doWait().untilPassing(() -> {
            assertEquals(bound, driver.findElement(By.cssSelector("#bound")).getText());
            assertEquals(direct, driver.findElement(By.cssSelector("#direct")).getText());
        });
    }
}
