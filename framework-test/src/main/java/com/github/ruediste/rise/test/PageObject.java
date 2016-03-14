package com.github.ruediste.rise.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
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
    private Supplier<? extends SearchContext> rootSearchContextSupplier;
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

    public Supplier<? extends SearchContext> getRootSearchContextSupplier() {
        return rootSearchContextSupplier;
    }

    protected WebElement rootElement() {
        SearchContext ctx = rootSearchContextSupplier.get();
        if (ctx instanceof WebElement)
            return (WebElement) ctx;
        else if (ctx instanceof WebDriver)
            return ctx.findElement(By.cssSelector("html"));
        throw new RuntimeException("Unable to determine root element for search context " + ctx);
    }

    public void setRootElementSupplier(Supplier<? extends SearchContext> rootSearchContextSupplier) {
        this.rootSearchContextSupplier = rootSearchContextSupplier;
    }

    protected WebElement findElement(By by) {
        return by.findElement(rootSearchContextSupplier.get());
    }

    protected List<WebElement> findElements(By by) {
        return by.findElements(rootSearchContextSupplier.get());
    }

    protected WebElement lazy(By by) {
        return (WebElement) Proxy.newProxyInstance(WebElement.class.getClassLoader(),
                new Class<?>[] { WebElement.class }, new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(findElement(by), args);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    protected List<WebElement> lazys(By by) {
        return (List<WebElement>) Proxy.newProxyInstance(WebElement.class.getClassLoader(),
                new Class<?>[] { List.class }, new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(findElements(by), args);
                    }
                });
    }

    protected WebElement cached(By by) {
        return (WebElement) Proxy.newProxyInstance(WebElement.class.getClassLoader(),
                new Class<?>[] { WebElement.class }, new InvocationHandler() {

                    WebElement element;

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        synchronized (this) {
                            if (element == null)
                                element = findElement(by);
                        }
                        return method.invoke(element, args);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    protected List<WebElement> cacheds(By by) {
        return (List<WebElement>) Proxy.newProxyInstance(WebElement.class.getClassLoader(),
                new Class<?>[] { List.class }, new InvocationHandler() {

                    List<WebElement> element;

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        synchronized (this) {
                            if (element == null)
                                element = findElements(by);
                        }
                        return method.invoke(element, args);
                    }
                });
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

    protected <T extends WebElement> WebElement getContainingReloadElement(T element) {
        return testUtil.getContainingReloadElement(element);
    }

    protected <T extends PageObject> T pageObject(Class<T> cls) {
        return testUtil.pageObject(cls, rootSearchContextSupplier);
    }

    protected <T extends PageObject> T pageObject(Class<T> cls, WebElement rootElement) {
        return pageObject(cls, () -> rootElement);
    }

    protected <T extends PageObject> T pageObject(Class<T> cls, Supplier<WebElement> rootElementSupplier) {
        return testUtil.pageObject(cls, rootElementSupplier);
    }

    protected <T extends PageObject> T pageObject(Class<T> cls, By by) {
        return testUtil.pageObject(cls, getRootSearchContextSupplier(), by);
    }

    /**
     * set the text using actions, solving some issues with the DOM events fired
     * when using {@link WebElement#clear()} and
     * {@link WebElement#sendKeys(CharSequence...)}
     */
    protected void setText(WebElement element, String text) {
        actions.click(element).sendKeys(Keys.END).keyDown(Keys.SHIFT).sendKeys(Keys.HOME).keyUp(Keys.SHIFT)
                .sendKeys(Keys.BACK_SPACE).sendKeys(text).perform();
    }
}
