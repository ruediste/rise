package com.github.ruediste.rise.nonReloadable.front.reload.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier.ClassChangeTransaction;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndexTest;
import com.github.ruediste.rise.nonReloadable.front.reload.Reloadable;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadableClassesIndexTestHelper;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadableClassesIndex;
import com.github.ruediste.rise.util.AsmUtil;

@RunWith(MockitoJUnitRunner.class)
@Reloadable
public class ReloadableClassesIndexTest {
    @Mock
    Logger log;

    @InjectMocks
    ReloadableClassesIndex cache;

    private Object tst;

    private class A {
    }

    @Before
    public void before() throws IOException {
        tst = new Object() {
        };
        ClassChangeTransaction trx = new ClassChangeNotifier.ClassChangeTransaction();
        trx.addedClasses
                .add(AsmUtil.readClass(ReloadableClassesIndexTest.class));
        trx.addedClasses.add(AsmUtil.readClass(A.class));
        trx.addedClasses.add(AsmUtil.readClass(tst.getClass()));
        ReloadableClassesIndexTestHelper.callOnChange(cache, trx);
    }

    @Test
    public void testInnerClass() {
        assertTrue(cache.isReloadable(A.class.getName()));
    }

    @Test
    public void testAnonymousClass() {
        assertTrue(cache.isReloadable(tst.getClass().getName()));
    }
}
