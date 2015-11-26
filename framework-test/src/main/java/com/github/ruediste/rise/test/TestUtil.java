package com.github.ruediste.rise.test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.github.ruediste.c3java.invocationRecording.MethodInvocation;
import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;

public interface TestUtil {
    public static int defaultWaitSeconds = 1;

    WebDriver internal_getDriver();

    default RiseWait<WebDriver> doWait() {
        return new RiseWait<>(internal_getDriver())
                .withTimeout(Duration.ofSeconds(defaultWaitSeconds));
    }

    default RiseWait<WebDriver> doWait(long timeOutInSeconds) {
        return new RiseWait<>(internal_getDriver())
                .withTimeout(Duration.ofSeconds(timeOutInSeconds));
    }

    default <T> void assertPage(Class<T> cls, Consumer<T> methodAccessor) {
        Method method = MethodInvocationRecorder
                .getLastInvocation(cls, methodAccessor).getMethod();
        String expectedPageName = method.getDeclaringClass().getName() + "."
                + method.getName();
        doWait().until(new Runnable() {

            @Override
            public void run() {
                assertThat(
                        internal_getDriver().findElement(By.tagName("body"))
                                .getAttribute("data-test-name"),
                        equalTo(expectedPageName));

            }

            @Override
            public String toString() {
                return "page name is " + expectedPageName;
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

    /**
     * Perform the given action and wait for the next page reload.
     */
    default <T extends WebElement> void waitForRefresh(T element,
            int timeoutSeconds, Consumer<T> action) {
        WebElement body = internal_getDriver().findElement(By.tagName("body"));
        String initialReloadCount = body.getAttribute("data-rise-reload-count");
        action.accept(element);
        doWait(timeoutSeconds)
                .untilTrue(new java.util.function.Predicate<WebDriver>() {
                    @Override
                    public boolean test(WebDriver d) {
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

    default <T extends PageObject> T pageObject(Class<T> cls) {
        return PageObjectFactory.createPageObject(internal_getDriver(), cls,
                Optional.empty());
    }

    default <T extends PageObject> T pageObject(Class<T> cls,
            Supplier<WebElement> rootElementFunction) {
        return PageObjectFactory.createPageObject(internal_getDriver(), cls,
                Optional.of(rootElementFunction));
    }

    default <T extends PageObject> T pageObject(Class<T> cls, By by) {
        return PageObjectFactory.createPageObject(internal_getDriver(), cls,
                Optional.of(() -> internal_getDriver().findElement(by)));
    }
}
