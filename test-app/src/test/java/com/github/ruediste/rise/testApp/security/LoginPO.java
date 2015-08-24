package com.github.ruediste.rise.testApp.security;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.testApp.PageObject;

public class LoginPO extends PageObject {

    public LoginPO(WebDriver driver) {
        super(driver);
        assertPage(LoginController.class, x -> x.index(null));
    }

    public void setUserName(String value) {
        WebElement element = driver.findElement(byDataTestName("userName"));
        element.clear();
        element.sendKeys(value);
    }

    public void setPassword(String value) {
        WebElement element = driver.findElement(byDataTestName("password"));
        element.clear();
        element.sendKeys(value);
    }

    public void login() {
        driver.findElement(byDataTestName("login")).click();
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
