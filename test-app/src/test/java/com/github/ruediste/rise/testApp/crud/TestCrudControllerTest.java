package com.github.ruediste.rise.testApp.crud;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.core.persistence.TransactionTemplate;
import com.github.ruediste.rise.testApp.WebTest;

public class TestCrudControllerTest extends WebTest {

    @Inject
    TestEntityFactory factory;

    @Inject
    TransactionTemplate trx;

    @Inject
    EntityManager em;

    private TestCrudEntityA a;
    private TestCrudEntityB b;

    @Before
    public void before() {
        if (a == null)
            a = factory.testCrudEntityA();
        if (b == null)
            b = factory.testCrudEntityB();

        driver.navigate().to(url(go(TestCrudController.class)
                .browse(TestCrudEntityA.class, null)));
    }

    @Test
    public void browseRightColumns() {
        CrudBrowserPO browser = pageObject(CrudBrowserPO.class);
        assertThat(browser.getColumnTestNames(),
                contains("stringValue", "actions"));
    }

    @Test
    public void browseFilter() {
        CrudBrowserPO browser = searchEntity();
        List<WebElement> rows = browser.getRows();
        assertThat(rows.size(), is(equalTo(1)));
    }

    /**
     * Search for all {@link TestCrudEntityA}s
     */
    private CrudBrowserPO searchEntity() {
        CrudBrowserPO browser = pageObject(CrudBrowserPO.class);
        browser.setFilter(
                dataTestName(TestCrudEntityA.class, x -> x.getStringValue()),
                a.getStringValue());
        browser.search();
        return browser;
    }

    @Test
    public void browseDisplay() {
        CrudBrowserPO browser = searchEntity();

        CrudDisplayPO display = browser.display(0);

        // check that right entity is shown
        assertThat(display.getPropertyText(
                dataTestName(TestCrudEntityA.class, x -> x.getStringValue())),
                equalTo(a.getStringValue()));

    }

    @Test
    public void browseEdit() {
        CrudBrowserPO browser = searchEntity();

        CrudEditPO edit = browser.edit(0);

        // check that right entity is shown
        assertThat(edit.getPropertyText(
                dataTestName(TestCrudEntityA.class, x -> x.getStringValue())),
                equalTo(a.getStringValue()));

    }

    @Test
    public void browseDelete() {
        CrudBrowserPO browser = searchEntity();

        CrudDeletePO delete = browser.delete(0);

        // check that right entity is shown
        assertThat(delete.getIdentification(),
                containsString(a.getStringValue()));

    }

    @Test
    public void displayShownProperties() {
        assertThat(searchEntity().display(0).getPropertyTestNames(),
                contains("id", "stringValue", "entityB"));
    }

    @Test
    public void displayBrowse() {
        searchEntity().display(0).browse();
    }

    @Test
    public void displayEdit() {
        CrudEditPO edit = searchEntity().display(0).edit();

        assertThat(edit.getPropertyText(
                dataTestName(TestCrudEntityA.class, x -> x.getStringValue())),
                equalTo(a.getStringValue()));
    }

    @Test
    public void displayDelete() {
        CrudDeletePO delete = searchEntity().display(0).delete();

        assertThat(delete.getIdentification(),
                containsString(a.getStringValue()));
    }

    @Test
    public void deleteDoDelete() {
        trx.execute(() -> {
            assertNotNull(loadA());
        });
        searchEntity().display(0).delete().delete();

        trx.execute(() -> {
            assertNull(loadA());
        });
    }

    private TestCrudEntityA loadA() {
        return em.find(TestCrudEntityA.class, a.getId());
    }

    @Test
    public void editShownProperties() {
        assertThat(searchEntity().edit(0).getPropertyTestNames(),
                contains("id", "stringValue", "entityB"));
    }

    @Test
    public void editPick() {

        CrudEditPO edit = searchEntity().edit(0).pick("entityB")
                .setFilter(
                        dataTestName(TestCrudEntityB.class, x -> x.getValue()),
                        b.getValue())
                .search().choose(0);

        assertThat(edit.getPropertyText(
                dataTestName(TestCrudEntityA.class, x -> x.getEntityB())),
                containsString(b.getValue()));

        edit.save();

        trx.execute(() -> {
            assertEquals(b.getId(), em.find(TestCrudEntityA.class, a.getId())
                    .getEntityB().getId());
        });
    }

    @Test
    public void editBrowse() {
        searchEntity().edit(0).browse();
    }

    @Test
    public void editSave() {
        searchEntity().edit(0).setProperty(
                dataTestName(TestCrudEntityA.class, x -> x.getStringValue()),
                "foo").save();
        trx.execute(() -> {
            assertEquals("foo", loadA().getStringValue());
        });
    }

}
