package com.github.ruediste.rise.core.persistence;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;

public class OwnEntityManagersTest extends DbTestBase {
    @Before
    public void before() {
        holder.setNewEntityManagerSet();
    }

    @After
    public void after() {
        holder.closeCurrentEntityManagers();
        holder.removeCurrentSet();
    }

    @Inject
    TransactionManager txm;

    @Inject
    EntityManager em;

    @OwnEntityManagers
    static class A {

        @Inject
        EntityManager em;

        public void persist(TestEntity e) {
            em.persist(e);
        }

        public String readValue(long id) {
            return em.find(TestEntity.class, id).getValue();
        }

        @SkipOfOwnEntityManagers
        public String readValueSkip(long id) {
            return em.find(TestEntity.class, id).getValue();
        }

    }

    @Inject
    A a;

    @Inject
    EntityManagerHolder holder;

    @Test
    public void testOwnEntityManagers() throws Throwable {
        txm.begin();

        TestEntity e = new TestEntity();
        e.setValue("foo");
        em.persist(e);

        TestEntity e1 = new TestEntity();
        e1.setValue("bar");
        e1.setId(e.getId());
        a.persist(e1);

        assertEquals("foo", a.readValueSkip(e.getId()));
        assertEquals("bar", a.readValue(e.getId()));
        txm.rollback();
    }

}
