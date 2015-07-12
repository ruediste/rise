package com.github.ruediste.rise.testApp.persistence;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;
import org.openqa.selenium.By;

import com.github.ruediste.rise.core.persistence.TransactionTemplate;
import com.github.ruediste.rise.testApp.WebTest;

public class EntityControllerMvcTest extends WebTest {

    @Inject
    TestEntityRepository rep;

    @Inject
    TransactionTemplate template;

    @Inject
    EntityManager em;

    @Test
    public void testIndex() throws Exception {
        TestEntity entity = new TestEntity();
        entity.setValue("Hello World");

        template.executor().updating().execute(() -> {
            em.createQuery("delete from TestEntity").executeUpdate();
            em.persist(entity);
        });

        driver.navigate().to(url(go(EntityControllerMvc.class).index()));

        Set<String> items = driver.findElements(By.cssSelector("li")).stream()
                .map(e -> e.getText()).collect(toSet());

        assertThat(items, contains(Objects.toString(entity.getId())));
    }

    @Test
    public void testDelete() throws Exception {
        TestEntity entity = new TestEntity();
        entity.setValue("Hello World");

        template.executor().updating().execute(() -> {
            em.persist(entity);
        });

        // check entity present
        template.executor().execute(() -> {
            assertNotNull(em.find(TestEntity.class, entity.getId()));
        });

        // delete
        driver.navigate().to(url(go(EntityControllerMvc.class).delete(entity)));

        // check entity has been deleted
        template.executor().execute(() -> {
            assertNull(em.find(TestEntity.class, entity.getId()));
        });
    }
}
