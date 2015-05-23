package com.github.ruediste.rise.crud;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CrudModelReaderTest {

    @InjectMocks
    CrudModelReader reader;

    private class A {
    }

    @Test
    public void testModelFound() {
        assertNotNull(reader.getCrudModel(A.class));
    }

    @Test
    public void propertyFound() {
        assertNotNull(reader.getCrudModel(A.class));
    }
}
