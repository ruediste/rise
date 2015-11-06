package com.github.ruediste.rise.testApp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverFactory {

    public static WebDriver createDriver() {
        if ("true".equalsIgnoreCase(System.getenv("TRAVIS")))
            return new FirefoxDriver();
        return new ChromeDriver();
    }
}
