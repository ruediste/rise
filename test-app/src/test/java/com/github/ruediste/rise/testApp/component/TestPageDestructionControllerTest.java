package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.testApp.WebTest;
import com.github.ruediste.rise.testApp.component.TestPageDestructionController.DestructionRecoreder;

public class TestPageDestructionControllerTest extends WebTest {

    @Inject
    DestructionRecoreder mgr;

    @Before
    public void before() {
        mgr.destroyed.clear();
    }

    @Test
    public void createDestroyImmediately() throws Exception {
        driver.navigate()
                .to(url(go(TestPageDestructionController.class).index(1)));
        assertPage(TestPageDestructionController.class, x -> x.index(1));

        driver.close();
        assertFalse(mgr.destroyed.contains(1));
        Thread.sleep(Duration.ofSeconds(4).toMillis());
        assertTrue(mgr.destroyed.contains(1));
    }

    @Test
    public void createNavigateAway() throws Exception {
        driver.navigate()
                .to(url(go(TestPageDestructionController.class).index(1)));
        assertPage(TestPageDestructionController.class, x -> x.index(1));
        driver.findElement(byDataTestName(TestPageDestructionController.class,
                x -> x.navigateAway())).click();
        assertPage(TestPageDestructionController.class, x -> x.index2());
        assertTrue(mgr.destroyed.contains(1));
    }

    @Test
    public void createInvalidateSession() throws Exception {
        driver.navigate().to(url(go(TestPageDestructionController.class)
                .indexInvalidateSession(1)));
        assertPage(TestPageDestructionController.class,
                x -> x.indexInvalidateSession(1));
        assertTrue(mgr.destroyed.contains(1));
    }
}
