package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.testApp.WebTest;

public class NonRenderedComponentsControllerTest extends WebTest {

    @Before
    public void before() {
        driver.navigate().to(url(go(NonRenderedComponentsController.class).index()));
    }

    @Test
    public void reloadDoesNotSetCheckboxToFalse() {
        driver.findElement(byDataTestName(NonRenderedComponentsController.class, x -> x.reload())).click();
        doWait().untilPassing(() -> assertEquals("2true", driver.findElement(byDataTestName("checked")).getText()));
    }
}
