package com.github.ruediste.rise.testApp.component;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.testApp.WebTest;
import com.google.common.base.Predicate;

public class TestAutoCompleteControllerTest extends WebTest {

    @Before
    public void before() {
        driver.navigate().to(url(go(TestAutoCompleteController.class).index()));
    }

    @Test
    public void searchPush_itemPushed() {
        driver.findElement(byDataTestName("entry")).sendKeys("Javasc");
        driver.findElement(byDataTestName("push")).click();
        doWait().until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver x) {
                return driver.findElement(byDataTestName("chosenEntry"))
                        .getText().equals("JavaScript");
            }
        });
    }
}
