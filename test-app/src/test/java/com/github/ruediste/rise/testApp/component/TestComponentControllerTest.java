package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.core.persistence.TransactionCallback;
import com.github.ruediste.rise.core.persistence.TransactionTemplate;
import com.github.ruediste.rise.testApp.WebTest;
import com.github.ruediste.rise.testApp.persistence.TestAppEntity;

public class TestComponentControllerTest extends WebTest {

    @Inject
    TransactionTemplate t;

    @Inject
    ComponentUtil util;
    @Inject
    EntityManager em;

    @Test
    public void testSubControllers() {

        // create test entity
        TestAppEntity entity = t.executor().updating()
                .execute(new TransactionCallback<TestAppEntity>() {

                    @Override
                    public TestAppEntity doInTransaction() {
                        TestAppEntity e = new TestAppEntity();
                        e.setValue("foo");
                        em.persist(e);
                        return e;
                    }
                });

        // check
        driver.navigate().to(
                url(util.go(TestComponentController.class).initialize(
                        entity.getId())));
        compare(driver, "foo", "foo", "foo");

        // modify entity in ctrl A
        driver.findElement(By.cssSelector("#a input")).clear();
        driver.findElement(By.cssSelector("#a input")).sendKeys("bar");
        driver.findElement(By.cssSelector("#a button.save")).click();

        // check
        compare(driver, "foo", "bar", "foo");

        // refresh main
        driver.findElement(By.cssSelector("#main button.refresh")).click();
        compare(driver, "bar", "bar", "foo");

        // refresh b
        driver.findElement(By.cssSelector("#b button.refresh")).click();
        compare(driver, "bar", "bar", "bar");

    }

    private void compare(WebDriver driver, String main, String a, String b) {
        assertEquals(main, driver.findElement(By.cssSelector("#mainValue"))
                .getText());
        assertEquals(a, driver.findElement(By.cssSelector("#a .value"))
                .getText());
        assertEquals(b, driver.findElement(By.cssSelector("#b .value"))
                .getText());
    }
}
