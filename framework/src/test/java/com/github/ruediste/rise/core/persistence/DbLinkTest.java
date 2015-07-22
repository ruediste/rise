package com.github.ruediste.rise.core.persistence;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.TransactionManager;

import org.junit.Test;

public class DbLinkTest extends DbTestBase {

    @Inject
    TransactionManager txm;

    @Inject
    EntityManagerFactory emf;

    @Test
    public void test() throws Exception {
        {
            txm.begin();
            EntityManager em = emf.createEntityManager();
            TestEntity entity = new TestEntity();
            entity.setValue("Hello");
            em.persist(entity);
            em.flush();
            txm.commit();
        }
        {
            txm.begin();
            EntityManager em = emf.createEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TestEntity> q = cb.createQuery(TestEntity.class);
            Root<TestEntity> root = q.from(TestEntity.class);
            q.select(root);
            List<TestEntity> resultList = em.createQuery(q).getResultList();
            assertEquals("available TestEntities", 1, resultList.size());
            assertEquals("Hello", resultList.get(0).getValue());
            txm.commit();
        }

    }

}
