package com.github.ruediste.rise.core.argumentSerializer;

import static org.junit.Assert.assertSame;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;

import com.github.ruediste.rise.core.persistence.DbTestBase;
import com.github.ruediste.rise.core.persistence.TestEntity;
import com.github.ruediste.rise.core.persistence.TestEntityDerived;
import com.github.ruediste.rise.core.persistence.TransactionTemplate;
import com.github.ruediste.rise.util.AnnotatedTypes;

public class EntityArgumentSerializerTest extends DbTestBase {

    @Inject
    TransactionTemplate template;

    @Inject
    EntityManager em;

    @Inject
    EntityArgumentSerializer serializer;

    @Test
    public void derivedHandledCorrectly() {
        template.execute(() -> {
            TestEntityDerived e = new TestEntityDerived();
            e.setDerivedValue("foo");
            em.persist(e);

            checkRoundTrip(e);
        });
    }

    @Test
    public void baseHandledCorrectly() {
        template.execute(() -> {
            TestEntity e = new TestEntity();
            e.setValue("foo");
            em.persist(e);

            checkRoundTrip(e);
        });
    }

    private void checkRoundTrip(Object e) {
        String serialized = serializer.generate(
                AnnotatedTypes.of(TestEntity.class), e);

        assertSame(
                e,
                serializer.parse(AnnotatedTypes.of(TestEntity.class),
                        serialized).get());
    }
}
