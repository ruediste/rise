package com.github.ruediste.rise.testApp.crud;

import org.junit.Test;

import com.github.ruediste.rise.testApp.WebTest;

public class TestCrudControllerTest extends WebTest {

    @Test
    public void browse() {
        driver.navigate().to(
                url(go(CrudInvocationController.class).browseTestEnties()));
    }
}
