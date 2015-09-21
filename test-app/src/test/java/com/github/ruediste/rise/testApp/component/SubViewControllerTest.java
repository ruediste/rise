package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.github.ruediste.rise.testApp.WebTest;

public class SubViewControllerTest extends WebTest {

    @Test
    public void test() throws InterruptedException {
        driver.navigate().to(url(go(SubViewController.class).index()));
        checkText("Foo");
        driver.findElement(By.cssSelector(".sub2")).click();
        Thread.sleep(100);
        checkText("bar");
        driver.findElement(By.cssSelector(".sub1")).click();
        Thread.sleep(100);
        checkText("Foo");
    }

    private void checkText(String expected) {
        assertEquals(expected,
                driver.findElement(By.cssSelector(".subText")).getText());
    }
}
