package com.github.ruediste.rise.crud;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.rise.crud.annotations.CrudStrategy;
import com.github.ruediste.salta.jsr330.Injector;

@RunWith(MockitoJUnitRunner.class)
public class CrudUtilTest {

    @Mock
    Injector injector;
    @Mock
    TestFactory testFactoryMock;

    TestFactoryImpl testFactoryImpl;

    @InjectMocks
    CrudUtil util;

    @Before
    public void before() {
        testFactoryImpl = new TestFactoryImpl();
    }

    private class A {
    }

    @CrudStrategy(type = TestFactory.class, implementation = TestFactoryImpl.class)
    private class B {
    }

    private interface TestFactory {

    }

    private static class TestFactoryImpl {

    }

    @Test
    public void testGetStrategy() throws Exception {
        when(injector.getInstance(TestFactory.class)).thenReturn(
                testFactoryMock);
        when(injector.getInstance(TestFactoryImpl.class)).thenReturn(
                testFactoryImpl);
        assertSame(testFactoryMock,
                util.getStrategy(TestFactory.class, A.class));
        assertSame(testFactoryImpl,
                util.getStrategy(TestFactory.class, B.class));
    }
}
