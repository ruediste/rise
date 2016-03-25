package com.github.ruediste.rise.testApp.crud;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.testApp.WebTest;

public class TestPropertyTypes extends WebTest {

    @Inject
    TestEntityFactory factory;

    @Inject
    TransactionControl trx;

    @Inject
    EntityManager em;

    private TestCrudEntityTypes entity;

    @Before
    public void before() {
        if (entity == null)
            entity = factory.testCrudEntityTypes();

        driver.navigate().to(url(go(TestCrudController.class).browse(TestCrudEntityTypes.class, null)));
    }

    /**
     * search for {@link #a}
     */
    private CrudBrowserPO searchEntity() {
        CrudBrowserPO browser = pageObject(CrudBrowserPO.class);
        browser.getFilterLong(dataTestName(TestCrudEntityTypes.class, x -> x.getId())).set(entity.getId());
        browser.search();
        return browser;
    }

    @Test
    public void testEditBoolean() {
        CrudEditPO edit = searchEntity().edit(0);
        edit.getPropertyBoolean(dataTestName(TestCrudEntityTypes.class, x -> x.isTestBoolean())).set(true);
        edit.save();
        reloadEntity();
        assertEquals(true, entity.isTestBoolean());
    }

    private void reloadEntity() {
        entity = trx.execute(() -> em.find(TestCrudEntityTypes.class, entity.getId()));
    }
}
