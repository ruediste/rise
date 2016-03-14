package com.github.ruediste.rise.testApp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

public class AssetReferencingControllerTest extends WebTest {

    @Before
    public void before() {
        driver.navigate().to(url(go(AssetReferencingController.class).index()));
    }

    @Test
    public void simple() {
        assertEquals("Hello", driver.findElement(By.cssSelector("#data")).getText());
    }

    @Test
    public void cssPresent() {
        checkCssPresent("#samePackage");
        checkCssPresent("#bundleClass");
        checkCssPresent("#default");
        checkCssPresent("#absolute");
    }

    private void checkCssPresent(String id) {
        assertEquals("right", driver.findElement(By.cssSelector(id)).getCssValue("text-align"));
    }
}
