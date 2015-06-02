package com.github.ruediste.rise.integration;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.github.ruediste.rise.component.components.CButton;

@RunWith(MockitoJUnitRunner.class)
public class RiseCanvasHelperTest {
    private static class TestCanvas extends RiseCanvasBase<TestCanvas> {

        @Override
        public TestCanvas self() {
            return this;
        }
    }

    @Mock
    Logger log;

    @InjectMocks
    RiseCanvasHelper helper;

    @InjectMocks
    TestCanvas html;

    ByteArrayOutputStream out;

    @Before
    public void before() {
        html.helper = helper;
        out = new ByteArrayOutputStream();
    }

    @Test
    public void testComponentOutput() {
        html.initializeForComponent(out);
        html.add(new CButton());
        html.div().CLASS("test")._div();
        html.flush();
        assertEquals(0, out.size());
    }
}
