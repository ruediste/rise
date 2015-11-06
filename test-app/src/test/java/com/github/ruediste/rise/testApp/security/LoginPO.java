package com.github.ruediste.rise.testApp.security;

import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.test.PageObject;

public class LoginPO extends PageObject {

    @Override
    protected void initialize() {
        assertPage(LoginController.class, x -> x.index(null));
    }

    public void setUserName(String value) {
        WebElement element = findElement(byDataTestName("userName"));
        element.clear();
        element.sendKeys(value);
    }

    public void setPassword(String value) {
        WebElement element = findElement(byDataTestName("password"));
        element.clear();
        element.sendKeys(value);
    }

    public void login() {
        findElement(byDataTestName("login")).click();
    }

    public void login(String user, String pwd) {
        setUserName(user);
        setPassword(pwd);
        login();
    }

    public void defaultLogin() {
        login("foo", "foo");
    }
}
