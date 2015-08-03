package com.github.ruediste.rise.testApp.crud;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;

public class PageObject {

    protected final WebDriver driver;

    protected PageObject(WebDriver driver) {
        this.driver = driver;
    }

    protected WebDriverWait doWait(long timeOutInSeconds) {
        return new WebDriverWait(driver, timeOutInSeconds);
    }

    protected <T> void assertPage(Class<T> cls, Consumer<T> methodAccessor) {
        Method method = MethodInvocationRecorder.getLastInvocation(cls,
                methodAccessor).getMethod();
        assertThat(
                driver.findElement(By.tagName("body")).getAttribute(
                        "data-test-name"), equalTo(method.getDeclaringClass()
                        .getName() + "." + method.getName()));
    }

    By byDataTestName(String name) {
        return By.cssSelector("*[data-test-name=\"" + name + "\"]");
    }
}
