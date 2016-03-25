package com.github.ruediste.rise.testApp.crud;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.RandomStringUtils;

import com.github.ruediste.rise.core.persistence.TransactionControl;

public class TestEntityFactory {

    @Inject
    TransactionControl trx;

    @Inject
    EntityManager em;

    public TestCrudEntityA testCrudEntityA() {
        TestCrudEntityA result = new TestCrudEntityA();
        result.setStringValue(RandomStringUtils.randomAlphanumeric(10));
        return save(result);
    }

    public TestCrudEntityTypes testCrudEntityTypes() {
        TestCrudEntityTypes result = new TestCrudEntityTypes();
        return save(result);
    }

    public TestCrudEntityB testCrudEntityB() {
        TestCrudEntityB result = new TestCrudEntityB();
        result.setValue(RandomStringUtils.randomAlphanumeric(10));
        return save(result);
    }

    private <T> T save(T result) {
        trx.updating().execute(() -> em.persist(result));
        return result;
    }

}
