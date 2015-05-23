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
import org.openqa.selenium.WebDriver;

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

        template.builder().updating().execute(trx -> {
            em.persist(entity);
            trx.commit();
        });

        WebDriver driver = createDriver();
        driver.navigate().to(url(go(EntityControllerMvc.class).index()));

        Set<String> items = driver.findElements(By.cssSelector("li")).stream()
                .map(e -> e.getText()).collect(toSet());

        assertThat(items, contains(Objects.toString(entity.getId())));
    }

    @Test
    public void testDelete() throws Exception {
        TestEntity entity = new TestEntity();
        entity.setValue("Hello World");

        template.builder().updating().execute(trx -> {
            em.persist(entity);
            trx.commit();
        });

        // check entity present
        template.builder().execute(trx -> {
            assertNotNull(em.find(TestEntity.class, entity.getId()));
        });

        // delete
        WebDriver driver = createDriver();
        driver.navigate().to(url(go(EntityControllerMvc.class).delete(entity)));

        // check entity has been deleted
        template.builder().execute(trx -> {
            assertNull(em.find(TestEntity.class, entity.getId()));
        });
    }
}
