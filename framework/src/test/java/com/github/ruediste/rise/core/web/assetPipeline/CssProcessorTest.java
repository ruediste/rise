package com.github.ruediste.rise.core.web.assetPipeline;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class CssProcessorTest {
    private CssProcessor processor;

    @Before
    public void before() {
        processor = new CssProcessor();
    }

    @Test
    public void testImportSimple() throws IOException {

        Asset combined = processor.combineStyleSheets(Arrays
                .asList(new TestAsset("main.css", "@import \"test.css\";")));
    }
}
