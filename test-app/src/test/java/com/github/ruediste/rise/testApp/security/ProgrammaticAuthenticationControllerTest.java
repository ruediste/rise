package com.github.ruediste.rise.testApp.security;

import javax.inject.Inject;

import org.junit.Test;
import org.openqa.selenium.Cookie;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.security.web.rememberMe.InMemoryRememberMeTokenDao;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeAuthenticationProvider;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeToken;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.testApp.WebTest;

public class ProgrammaticAuthenticationControllerTest extends WebTest {

    @Inject
    InMemoryRememberMeTokenDao dao;

    @Inject
    CoreConfiguration config;

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
        deleteAllCookies();
        loadAuthRequired();
        new LoginPO(driver).defaultLogin();
        assertPage(ProgrammaticAuthenticationController.class,
                x -> x.authenticationRequired());
    }

    @Test
    public void testAuthenticationRequired_tokenTheft() throws Exception {
        deleteAllCookies();
        loadAuthRequired();
        new LoginPO(driver).defaultLogin();

        // modify stored token
        Cookie cookie = driver.manage().getCookieNamed(
                config.rememberMeCookieName);
        String value = cookie.getValue();
        if (value.startsWith("\""))
            value = value.substring(1);
        if (value.endsWith("\""))
            value = value.substring(0, value.length() - 1);
        RememberMeToken token = RememberMeAuthenticationProvider
                .parseToken(value);
        dao.updateToken(token.withToken(new byte[] { 1 }));

        // try remember me
        clearSession();
        loadAuthRequired();
        assertPage(LoginController.class, x -> x.tokenTheftDetected(null));
    }

    @Test
    public void testAuthenticationRequired_useRememberMe() throws Exception {
        // perform normal login
        deleteAllCookies();
        loadAuthRequired();
        new LoginPO(driver).defaultLogin();
        assertPage(ProgrammaticAuthenticationController.class,
                x -> x.authenticationRequired());

        // clear session, uses remember me
        clearSession();
        loadAuthRequired();
        assertPage(ProgrammaticAuthenticationController.class,
                x -> x.authenticationRequired());

        deleteAllCookies();
        loadAuthRequired();
        new LoginPO(driver);
    }

    private void deleteAllCookies() {
        driver.manage().deleteAllCookies();
        driver.get(url(new PathInfo("")));
    }

    private void clearSession() {
        driver.manage().deleteCookieNamed("JSESSIONID");
    }

    private void loadAuthRequired() {
        driver.navigate().to(
                url(go(ProgrammaticAuthenticationController.class)
                        .authenticationRequired()));
    }

}
