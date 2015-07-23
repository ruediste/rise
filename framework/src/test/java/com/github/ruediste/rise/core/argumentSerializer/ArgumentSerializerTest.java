package com.github.ruediste.rise.core.argumentSerializer;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.lang.reflect.AnnotatedType;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.persistence.DbTestBase;
import com.github.ruediste.rise.core.persistence.TestEntity;
import com.github.ruediste.rise.core.persistence.TestEntityDerived;
import com.github.ruediste.rise.core.persistence.TransactionTemplate;
import com.github.ruediste.rise.util.AnnotatedTypes;

public class ArgumentSerializerTest extends DbTestBase {

    @Inject
    TransactionTemplate template;

    @Inject
    EntityManager em;

    @Inject
    CoreConfiguration config;

    private static class A implements Serializable {
        private static final long serialVersionUID = 1L;
        String value;

        A(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            A other = (A) obj;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

    }

    @Test
    public void testInt() {
        checkRoundTrip(Integer.TYPE, 2);
        checkRoundTrip(Integer.TYPE, 0);
        checkRoundTrip(Integer.class, 4);
        checkRoundTrip(Integer.class, null);
        checkRoundTrip(Object.class, 4);
        checkRoundTrip(Object.class, null);
    }

    @Test
    public void testLong() {
        checkRoundTrip(Long.TYPE, 2L);
        checkRoundTrip(Long.TYPE, 0L);
        checkRoundTrip(Long.class, 4L);
        checkRoundTrip(Long.class, null);
        checkRoundTrip(Object.class, 4L);
        checkRoundTrip(Object.class, null);
    }

    @Test
    public void testString() {
        checkRoundTrip(String.class, "foo");
        checkRoundTrip(String.class, null);
        checkRoundTrip(Object.class, "foo");
        checkRoundTrip(Object.class, null);
    }

    @Test
    public void testSerializable() {
        checkRoundTrip(A.class, new A("foo"));
        checkRoundTrip(A.class, null);
        checkRoundTrip(Object.class, new A("foo"));
        checkRoundTrip(Object.class, null);
    }

    @Test
    public void derivedHandledCorrectly() {
        template.execute(() -> {
            TestEntityDerived e = new TestEntityDerived();
            e.setDerivedValue("foo");
            em.persist(e);

            checkRoundTrip(TestEntity.class, e);
            checkRoundTrip(Object.class, e);
        });
    }

    @Test
    public void baseHandledCorrectly() {
        template.execute(() -> {
            TestEntity e = new TestEntity();
            e.setValue("foo");
            em.persist(e);

            checkRoundTrip(TestEntity.class, e);
            checkRoundTrip(Object.class, e);
        });
    }

    private void checkRoundTrip(Class<?> type, Object e) {
        AnnotatedType annotatedType = AnnotatedTypes.of(type);
        String serialized = config.generateArgument(annotatedType, e);

        assertEquals(e, config.parseArgument(annotatedType, serialized).get());
    }
}
