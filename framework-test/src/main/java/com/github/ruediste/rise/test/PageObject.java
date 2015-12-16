package com.github.ruediste.rise.test;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class PageObject {

    protected WebDriver driver;
    protected Actions actions;
    private Optional<Supplier<WebElement>> rootElementSupplier;
    private final TestUtilImpl testUtil = new TestUtilImpl();

    /**
     * Called after fields have been initialized. No need to call super()
     */
    protected void initialize() {

    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
        actions = new Actions(driver);
        testUtil.setDriver(driver);
    }

    public Optional<Supplier<WebElement>> getRootElementSupplier() {
        return rootElementSupplier;
    }

    public WebElement rootElement() {
        return getRootElementSupplier()
                .orElseThrow(
                        () -> new RuntimeException("No root element defined"))
                .get();
    }

    public void setRootElementSupplier(
            Optional<Supplier<WebElement>> rootElementSupplier) {
        this.rootElementSupplier = rootElementSupplier;
    }

    private SearchContext getSearchContext() {
        return rootElementSupplier.map(x -> (SearchContext) x.get())
                .orElse(driver);
    }

    protected WebElement findElement(By by) {
        return by.findElement(getSearchContext());
    }

    protected List<WebElement> findElements(By by) {
        return by.findElements(getSearchContext());
    }

    protected RiseWait<WebDriver> doWait() {
        return testUtil.doWait();
    }

    protected RiseWait<WebDriver> doWait(long timeOutInSeconds) {
        return testUtil.doWait(timeOutInSeconds);
    }

    protected <T> void assertPage(Class<T> cls, Consumer<T> methodAccessor) {
        testUtil.assertPage(cls, methodAccessor);
    }

    protected By byDataTestName(String name) {
        return testUtil.byDataTestName(name);
    }

    protected <T> String dataTestSelector(Class<T> cls, Consumer<T> accessor) {
        return testUtil.dataTestSelector(cls, accessor);
    }

    protected String dataTestSelector(String name) {
        return testUtil.dataTestSelector(name);
    }

    protected <T> By byDataTestName(Class<T> cls, Consumer<T> accessor) {
        return testUtil.byDataTestName(cls, accessor);
    }

    protected <T> String dataTestName(Class<T> cls, Consumer<T> accessor) {
        return testUtil.dataTestName(cls, accessor);
    }

    protected void clickAndWaitForRefresh(WebElement element) {
        testUtil.clickAndWaitForRefresh(element);
    }

    protected void clickAndWaitForRefresh(WebElement element, int waitSeconds) {
        testUtil.clickAndWaitForRefresh(element, waitSeconds);
    }

    protected void executeAndWaitForRefresh(Runnable action) {
        testUtil.executeAndWaitForRefresh(action);
    }

    protected void executeAndWaitForRefresh(Runnable action, int timeoutSeconds) {
        testUtil.executeAndWaitForRefresh(action, timeoutSeconds);
    }

    protected <T extends WebElement> WebElement getContainingReloadElement(
            T element) {
        return testUtil.getContainingReloadElement(element);
    }

    protected <T extends PageObject> T pageObject(Class<T> cls) {
        return testUtil.pageObject(cls);
    }

    protected <T extends PageObject> T pageObject(Class<T> cls,
            Supplier<WebElement> rootElementFunction) {
        return testUtil.pageObject(cls, rootElementFunction);
    }

    protected <T extends PageObject> T pageObject(Class<T> cls, By by) {
        return testUtil.pageObject(cls, by);
    }

    /**
     * set the text using actions, solving some issues with the DOM events fired
     * when using {@link WebElement#clear()} and
     * {@link WebElement#sendKeys(CharSequence...)}
     */
    protected void setText(WebElement element, String text) {
        actions.click(element).sendKeys(Keys.END).keyDown(Keys.SHIFT)
                .sendKeys(Keys.HOME).keyUp(Keys.SHIFT).sendKeys(Keys.BACK_SPACE)
                .sendKeys(text).perform();
    }
}
