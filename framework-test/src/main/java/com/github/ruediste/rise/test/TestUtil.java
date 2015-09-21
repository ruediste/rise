package com.github.ruediste.rise.test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import junit.framework.AssertionFailedError;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.ruediste.c3java.invocationRecording.MethodInvocation;
import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.google.common.base.Predicate;

public interface TestUtil {
    public static int defaultWaitSeconds = 1;

    WebDriver internal_getDriver();

    default WebDriverWait doWait() {
        return new WebDriverWait(internal_getDriver(), defaultWaitSeconds);
    }

    default FluentWait<WebDriver> doWait(long timeOutInSeconds) {
        return new WebDriverWait(internal_getDriver(), timeOutInSeconds)
                .ignoring(StaleElementReferenceException.class);
    }

    default <T> void assertPage(Class<T> cls, Consumer<T> methodAccessor) {
        Method method = MethodInvocationRecorder
                .getLastInvocation(cls, methodAccessor).getMethod();
        doWait().ignoring(AssertionFailedError.class,
                StaleElementReferenceException.class)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver x) {
                        assertThat(
                                internal_getDriver()
                                        .findElement(By.tagName("body"))
                                        .getAttribute("data-test-name"),
                                equalTo(method.getDeclaringClass().getName()
                                        + "." + method.getName()));
                        return true;
                    }
                });
    }

    default By byDataTestName(String name) {
        return By.cssSelector(dataTestSelector(name));
    }

    default <T> String dataTestSelector(Class<T> cls, Consumer<T> accessor) {
        return dataTestSelector(dataTestName(cls, accessor));
    }

    default String dataTestSelector(String name) {
        return "*[data-test-name=\"" + name + "\"] ";
    }

    default <T> By byDataTestName(Class<T> cls, Consumer<T> accessor) {
        return byDataTestName(dataTestName(cls, accessor));
    }

    default <T> String dataTestName(Class<T> cls, Consumer<T> accessor) {
        MethodInvocation<Object> invocation = MethodInvocationRecorder
                .getLastInvocation(cls, accessor);
        Optional<PropertyInfo> property = PropertyUtil
                .tryGetAccessedProperty(invocation);
        return property.map(p -> p.getName())
                .orElseGet(() -> invocation.getMethod().getName());
    }

    /**
     * wait for refresh after clicking the element
     */
    default void clickAndWaitForRefresh(WebElement element) {
        clickAndWaitForRefresh(element, defaultWaitSeconds);
    }

    /**
     * wait for refresh after clicking the element
     */
    default void clickAndWaitForRefresh(WebElement element, int waitSeconds) {
        waitForRefresh(element, waitSeconds, x -> x.click());
    }

    default <T extends WebElement> void waitForRefresh(T element,
            Consumer<T> action) {
        waitForRefresh(element, defaultWaitSeconds, action);
    }

    default <T extends WebElement> void waitForRefresh(T element,
            int timeoutSeconds, Consumer<T> action) {
        WebElement body = internal_getDriver().findElement(By.tagName("body"));
        String initialReloadCount = body.getAttribute("data-rise-reload-count");
        action.accept(element);
        doWait(timeoutSeconds).ignoring(StaleElementReferenceException.class)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver d) {
                        String currentReloadCount = body
                                .getAttribute("data-rise-reload-count");
                        return !Objects.equals(initialReloadCount,
                                currentReloadCount);
                    }

                    @Override
                    public String toString() {
                        return "reload";
                    }
                });
    }

    default <T extends WebElement> WebElement getContainingReloadElement(
            T element) {
        WebElement reload = element.findElement(
                By.xpath("ancestor::*[contains(@class,'rise_reload')]"));
        return reload;
    }
}
