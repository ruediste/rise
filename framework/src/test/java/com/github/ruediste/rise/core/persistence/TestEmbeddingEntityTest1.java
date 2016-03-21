package com.github.ruediste.rise.core.persistence;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;

public class TestEmbeddingEntityTest1 extends DbTestBase {

    @Inject
    EntityManager em;

    @Inject
    @Unit1
    EntityManager em1;

    @Inject
    TransactionControl txc;

    @Test
    public void checkEmbeddableSeparated() {
        TestEmbeddingEntity e = new TestEmbeddingEntity();
        e.embeddableA.setStart(7);

        txc.updating().execute(() -> em.persist(e));
        e.embeddableA.setStart(0);
        TestEmbeddingEntity loaded = txc.execute(() -> em.find(TestEmbeddingEntity.class, e.id));

        assertEquals(7, loaded.embeddableA.getStart());
        assertEquals(0, loaded.embeddableB.getStart());
    }

    @Test
    public void testNullUnit() {
        TestEmbeddingEntityUnit1 e = new TestEmbeddingEntityUnit1();
        txc.updating().execute(() -> em1.persist(e));
        txc.updating().execute(() -> em.persist(e));
    }

    @Test
    public void testAnyUnit() {
        TestEntityAnyUnit e = new TestEntityAnyUnit();
        txc.updating().execute(() -> em1.persist(e));
        txc.updating().execute(() -> em.persist(e));
    }
}
