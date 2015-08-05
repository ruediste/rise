package com.github.ruediste.rise.testApp.crud;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

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

    private TestCrudEntityA a;

    @Before
    public void before() {
        if (a == null)
            a = factory.testCrudEntityA();
        driver.navigate().to(
                url(go(TestCrudController.class).browse(TestCrudEntityA.class,
                        null)));
    }

    @Test
    public void browseRightColumns() {
        CrudBrowserPO browser = new CrudBrowserPO(driver);
        assertThat(browser.getColumnTestNames(),
                contains("stringValue", "actions"));
    }

    @Test
    public void browseFilter() {
        CrudBrowserPO browser = searchEntity();
        List<WebElement> rows = browser.getRows();
        assertThat(rows.size(), is(equalTo(1)));
    }

    private CrudBrowserPO searchEntity() {
        CrudBrowserPO browser = new CrudBrowserPO(driver);
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
        assertThat(display.getPropertyText(dataTestName(TestCrudEntityA.class,
                x -> x.getStringValue())), equalTo(a.getStringValue()));

    }

    @Test
    public void browseEdit() {
        CrudBrowserPO browser = searchEntity();

        CrudEditPO edit = browser.edit(0);

        // check that right entity is shown
        assertThat(
                edit.getPropertyText(dataTestName(TestCrudEntityA.class,
                        x -> x.getStringValue())), equalTo(a.getStringValue()));

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
        assertThat(searchEntity().display(0).getShownProperties(),
                contains("id", "stringValue", "entityB"));
    }

    @Test
    public void displayBrowse() {
        searchEntity().display(0).browse();
    }

    @Test
    public void displayEdit() {
        CrudEditPO edit = searchEntity().display(0).edit();

        assertThat(
                edit.getPropertyText(dataTestName(TestCrudEntityA.class,
                        x -> x.getStringValue())), equalTo(a.getStringValue()));
    }

    @Test
    public void displayDelete() {
        CrudDeletePO delete = searchEntity().display(0).delete();

        assertThat(delete.getIdentification(),
                containsString(a.getStringValue()));
    }

}
