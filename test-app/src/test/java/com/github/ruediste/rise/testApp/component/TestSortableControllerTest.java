package com.github.ruediste.rise.testApp.component;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.github.ruediste.rise.testApp.WebTest;

public class TestSortableControllerTest extends WebTest {

    private Actions actions;

    @Before
    public void before() {
        driver.navigate().to(url(go(TestSortableController.class).index()));
        actions = new Actions(driver);
    }

    @Test
    public void testChangeOrderAndPushDown_orderChanged() {
        changeOrder();
        pushDown();
        doWait().untilTrue(
                () -> driver.findElement(byDataTestName("controllerStatus"))
                        .getText().equals("[bar, foo, fooBar]"));
    }

    @Test
    public void testChangeOrderAndPullUp_orderRestored() {
        changeOrder();
        pullUp();
        doWait().untilTrue(() -> contains("foo", "bar", "fooBar").matches(driver
                .findElements(By.cssSelector(
                        dataTestSelector(TestSortableController.Data.class,
                                x -> x.getItems()) + "> li"))
                .stream().map(e -> e.getText()).collect(toList())));
    }

    @Test
    public void testChangeOrder_PushDown_PullUp_orderKept() {
        changeOrder();
        pushDown();
        pullUp();
        doWait().untilTrue(() -> contains("bar", "foo", "fooBar").matches(driver
                .findElements(By.cssSelector(
                        dataTestSelector(TestSortableController.Data.class,
                                x -> x.getItems()) + "> li"))
                .stream().map(e -> e.getText()).collect(toList())));
    }

    private void pullUp() {
        driver.findElement(
                byDataTestName(TestSortableController.class, x -> x.pullUp()))
                .click();
    }

    private void pushDown() {
        driver.findElement(
                byDataTestName(TestSortableController.class, x -> x.pushDown()))
                .click();
    }

    private void changeOrder() {
        WebElement foo = driver.findElement(byDataTestName("foo"));
        WebElement bar = driver.findElement(byDataTestName("bar"));
        actions.clickAndHold(bar)
                .moveByOffset(0, foo.getLocation().y - bar.getLocation().y - 10)
                .release().perform();
    }
}