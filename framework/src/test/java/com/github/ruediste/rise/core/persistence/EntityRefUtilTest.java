package com.github.ruediste.rise.core.persistence;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;

import com.github.ruediste1.lambdaPegParser.Var;

public class EntityRefUtilTest extends DbTestBase {

    @Inject
    TransactionControl template;

    @Inject
    EntityRefUtil util;

    @Inject
    EntityManager em;

    @Inject
    @Unit1
    EntityManager em1;

    @Test
    public void toEntityRef() {
        Var<EntityRef<TestEntity>> ref = new Var<>();
        Var<EntityRef<TestEntity>> ref1 = new Var<>();
        template.updating().execute(() -> {
            TestEntity e = new TestEntity();
            e.setValue("foo");
            em.persist(e);
            ref.setValue(util.toEntityRef(e));

            e = new TestEntity();
            e.setValue("bar");
            em1.persist(e);
            ref1.setValue(util.toEntityRef(e));
        });

        template.execute(() -> {
            assertEquals("foo", util.load(ref.getValue()).getValue());
            assertEquals("bar", util.load(ref1.getValue()).getValue());
        });
    }
}
