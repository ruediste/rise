package com.github.ruediste.rise.testApp.startupError;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLink;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.testApp.app.TestAppFrontServlet;
import com.github.ruediste.rise.testApp.app.TestRestartableApplication;
import com.github.ruediste.rise.testApp.persistence.TestAppEntity;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.MembersInjector;

/**
 * Tests a startup error after injecting the non-restartable application
 */
public class StartupErrorAfterNonRestartableInjectionTest
        extends StartupErrorTest {
    @Override
    protected final FrontServletBase createServlet(Object testCase) {
        TestRestartableApplication app = new TestRestartableApplication() {

            @Override
            protected void startImpl(Injector permanentInjector) {
            }
        };

        FrontServletBase frontServlet = new TestAppFrontServlet(app) {
            private static final long serialVersionUID = 1L;

            @Inject
            MembersInjector<StartupErrorAfterNonRestartableInjectionTest> membersInjector;

            @Override
            protected void initImpl() throws Exception {
                super.initImpl();
                membersInjector.injectMembers(
                        StartupErrorAfterNonRestartableInjectionTest.this);
                throw new RuntimeException("My Error");
            }
        };

        return frontServlet;
    }

    @Test
    public void testErrorMessage() {
        driver.navigate().to(getBaseUrl());
        assertTrue(driver.getTitle().contains("Startup Error"));
        assertTrue(driver.getPageSource().contains("My Error"));

        // make sure drop-and-create database is present
        assertTrue(driver.getPageSource().contains("reate"));
    }

    @Inject
    DataBaseLinkRegistry registry;

    @Inject
    TransactionManager txm;

    @Test
    @Ignore("needs to be run individually")
    public void testDropAndCreate() throws Throwable {
        driver.navigate().to(getBaseUrl());
        assertTrue(driver.getTitle().contains("Startup Error"));

        DataBaseLink link = registry.getLink(null);
        EntityManagerFactory emf = link.getPersistenceUnitManager()
                .getEntityManagerFactory();

        // test without generating schema
        try {
            txm.begin();
            EntityManager em = emf.createEntityManager();
            TestAppEntity e = new TestAppEntity();
            em.persist(e);
            txm.commit();
            fail();
        } catch (DatabaseException e) {
            // swallow
        } finally {
            try {
                txm.rollback();
            } catch (Throwable t) {
                // swallow
            }
        }

        // hit create schema button
        driver.findElement(By.cssSelector("input")).click();

        // now it should work
        try {
            txm.begin();
            EntityManager em = emf.createEntityManager();
            TestAppEntity e = new TestAppEntity();
            em.persist(e);
            txm.commit();
        } finally {
            try {
                txm.rollback();
            } catch (Throwable t) {
                // swallow
            }
        }

    }

}
