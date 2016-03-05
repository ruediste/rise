package com.github.ruediste.rise.testApp.persistence;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;
import org.openqa.selenium.By;

import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.testApp.WebTest;

public class EntityControllerMvcTest extends WebTest {

    @Inject
    TestEntityRepository rep;

    @Inject
    TransactionControl template;

    @Inject
    EntityManager em;

    @Test
    public void testIndex() throws Exception {
        TestAppEntity entity = new TestAppEntity();
        entity.setValue("Hello World");

        template.updating().execute(() -> {
            em.createQuery("delete from TestAppEntity").executeUpdate();
            em.persist(entity);
        });

        driver.navigate().to(url(go(EntityControllerMvc.class).index()));

        Set<String> items = driver.findElements(By.cssSelector("li")).stream()
                .map(e -> e.getText()).collect(toSet());

        assertThat(items, contains(Objects.toString(entity.getId())));
    }

    @Test
    public void testIndexNoTransaction() throws Exception {

        driver.navigate()
                .to(url(go(EntityControllerMvc.class).indexNoTransaction()));

        assertThat(driver.getPageSource(),
                containsString("No EntityManagerSet is currently set"));
    }

    @Test
    public void testDelete() throws Exception {
        TestAppEntity entity = new TestAppEntity();
        entity.setValue("Hello World");

        template.executor().updating().execute(() -> {
            em.persist(entity);
        });

        // check entity present
        template.executor().execute(() -> {
            assertNotNull(em.find(TestAppEntity.class, entity.getId()));
        });

        // delete
        template.executor().execute(() -> {
            driver.navigate().to(url(
                    go(EntityControllerMvc.class).delete(em.merge(entity))));
        });

        // check entity has been deleted
        template.executor().execute(() -> {
            assertNull(em.find(TestAppEntity.class, entity.getId()));
        });
    }
}
