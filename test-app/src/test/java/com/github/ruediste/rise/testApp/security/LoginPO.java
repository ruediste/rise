package com.github.ruediste.rise.testApp.security;

import static org.junit.Assert.fail;

import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.testApp.PageObject;

public class LoginPO extends PageObject {

    public LoginPO(WebDriver driver) {
        super(driver);
        fail();
    }

    public void login(String user, String pwd) {

    }

    public void defaultLogin() {
        login("bob", "marley");
    }
}
