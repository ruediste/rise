package com.github.ruediste.rise.core.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.junit.Test;

import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;

public class EntityManagerTest extends DbTestBase {

    @Inject
    TransactionManager txm;

    @Inject
    EntityManager em;

    @Inject
    EntityManagerHolder holder;

    @Test
    public void testSeparateTx() throws Exception {
        long id;
        {
            txm.begin();
            holder.setNewEntityManagerSet();
            TestEntity entity = new TestEntity();
            entity.setValue("Hello");
            em.persist(entity);
            em.flush();
            txm.commit();
            id = entity.getId();
        }
        {
            txm.begin();
            holder.setNewEntityManagerSet();
            TestEntity entity = em.find(TestEntity.class, id);
            assertNotNull(entity);
            assertEquals("Hello", entity.getValue());
            txm.commit();
        }
    }

    @Test
    public void testKeepOpen() throws Exception {
        // create and persist
        TestEntity entity;
        {
            txm.begin();
            holder.setNewEntityManagerSet();
            assertTrue(em.isJoinedToTransaction());
            entity = new TestEntity();
            entity.setValue("Hello");
            em.persist(entity);
            em.flush();
            txm.commit();
        }

        assertTrue(em.isOpen());
        assertFalse(em.isJoinedToTransaction());
        assertTrue(em.contains(entity));

        // without tx, but EM open
        {
            txm.begin();
            assertFalse(em.isJoinedToTransaction());
            entity.setValue("Hello1");

            // does not close the em
            txm.rollback();
        }

        assertTrue(em.isOpen());
        assertFalse(em.isJoinedToTransaction());
        assertTrue(em.contains(entity));

        // finally in tx again
        {
            txm.begin();
            holder.joinTransaction();
            assertTrue(em.isJoinedToTransaction());
            assertTrue(em.contains(entity));
            txm.rollback();

            // entity becomes detached by rollback
            assertFalse(em.contains(entity));
        }

        assertTrue(em.isOpen());
        assertFalse(em.isJoinedToTransaction());

    }

    @Test
    public void testKeepOpenAndRefresh() throws Exception {
        // create and persist
        TestEntity entity;
        {
            txm.begin();
            holder.setNewEntityManagerSet();
            assertTrue(em.isJoinedToTransaction());
            entity = new TestEntity();
            entity.setValue("Hello");
            em.persist(entity);
            txm.commit();
        }
        em.refresh(entity);
    }
}
