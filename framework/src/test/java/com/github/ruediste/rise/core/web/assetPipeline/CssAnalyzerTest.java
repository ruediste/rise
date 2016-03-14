package com.github.ruediste.rise.core.web.assetPipeline;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.rise.core.web.assetPipeline.CssAnalyzer.CssProcessorHandler;

@RunWith(MockitoJUnitRunner.class)
public class CssAnalyzerTest {

    @InjectMocks
    CssAnalyzer processor;

    @Before
    public void before() {
    }

    @Test
    public void testImportSimple() throws IOException {
        checkProcessing("/*foo/* <Imported test.css;>", "/*foo/* @import \"test.css\";");
        checkProcessing("/*foo/* <Imported test.css;>", "/*foo/* @import  'test.css' ;");
        checkProcessing("/*foo/* <Imported test.css;screen,print>", "/*foo/* @import  'test.css' screen,print ;");
    }

    @Test
    public void testRefUrl() throws IOException {
        checkProcessing(
                " src: url(\"<ref ../fonts/glyphicons-halflings-regular.eot>\");\n"
                        + "  src: url(\"<ref ../fonts/glyphicons-halflings-regular.eot?#iefix>\") format('embedded-opentype'), url(\"<ref ../fonts/glyphicons-halflings-regular.ttf>\") format('truetype'), url(\"<ref ../fonts/glyphicons-halflings-regular.svg#glyphicons_halflingsregular>\") format('svg');\n",
                " src: url('../fonts/glyphicons-halflings-regular.eot');\n"
                        + "  src: url('../fonts/glyphicons-halflings-regular.eot?#iefix') format('embedded-opentype'), url(\"../fonts/glyphicons-halflings-regular.ttf\") format('truetype'), url('../fonts/glyphicons-halflings-regular.svg#glyphicons_halflingsregular') format('svg');\n");
    }

    @Test
    public void testImportWithUrl() throws IOException {
        checkProcessing("/*foo/* <Imported test.css;> url(\"<ref bar>\");",
                "/*foo/* @import url(\"test.css\"); url('bar');");
        checkProcessing("/*foo/* <Imported test.css;>", "/*foo/* @import  url('test.css');");
        checkProcessing("/*foo/* <Imported test.css;print>", "/*foo/* @import  url('test.css') print;");
        checkProcessing("/*foo/* @import  url(\"<impRef test.css>\") print;", "/*foo/* @import  url('test.css') print;",
                false);
    }

    private void checkProcessing(String expected, String css) {
        checkProcessing(expected, css, true);
    }

    private void checkProcessing(String expected, String css, boolean shouldImport) {
        StringBuffer sb = new StringBuffer();
        processor.process(css, sb, new CssProcessorHandler() {

            @Override
            public String replaceRef(String ref) {
                return "<ref " + ref + ">";
            }

            @Override
            public boolean shouldInline(String ref, String media) {
                return shouldImport;
            }

            @Override
            public String replaceImportRef(String ref) {
                return "<impRef " + ref + ">";
            }

            @Override
            public void performInline(StringBuffer sb, String ref, String media) {
                sb.append("<Imported " + ref + ";" + media + ">");
            }
        });
        assertEquals(expected, sb.toString());
    }
}
