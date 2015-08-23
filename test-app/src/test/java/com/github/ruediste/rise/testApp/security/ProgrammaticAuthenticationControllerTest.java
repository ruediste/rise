package com.github.ruediste.rise.testApp.security;

import org.junit.Test;

import com.github.ruediste.rise.testApp.WebTest;

public class ProgrammaticAuthenticationControllerTest extends WebTest {

    @Test
    public void testNoAuthenticationRequired() throws Exception {
        driver.navigate().to(
                url(go(ProgrammaticAuthenticationController.class)
                        .noAuthenticationRequired()));
        assertPage(ProgrammaticAuthenticationController.class,
                x -> x.noAuthenticationRequired());
    }

    @Test
    public void testAuthenticationRequired() throws Exception {
        driver.navigate().to(
                url(go(ProgrammaticAuthenticationController.class)
                        .authenticationRequired()));
        new LoginPO(driver).defaultLogin();
        assertPage(ProgrammaticAuthenticationController.class,
                x -> x.authenticationRequired());
    }

}
