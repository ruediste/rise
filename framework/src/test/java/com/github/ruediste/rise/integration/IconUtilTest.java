package com.github.ruediste.rise.integration;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;

public class IconUtilTest {

    @IconAnnotation
    @Retention(RetentionPolicy.RUNTIME)
    private @interface TestIcon {
        Glyphicon value();
    }

    @Stereotype
    @Retention(RetentionPolicy.RUNTIME)
    @TestIcon(Glyphicon.asterisk)
    private @interface TestAction {
    }

    interface A {
        @TestIcon(Glyphicon.apple)
        void foo();

        void bar();

        @TestAction
        void foobar();
    }

    static class B implements A {

        @Override
        public void foo() {
        }

        @TestIcon(Glyphicon.alert)
        @Override
        public void bar() {

        }

        @Override
        public void foobar() {

        }
    }

    IconUtil util = new IconUtil();

    @Test
    public void testGetIcon() throws Exception {
        assertEquals(Glyphicon.apple, util.getIcon(A.class, x -> x.foo()));
        assertEquals(Glyphicon.apple, util.getIcon(B.class, x -> x.foo()));
        assertEquals(Glyphicon.alert, util.getIcon(B.class, x -> x.bar()));
    }

    @Test
    public void testGetIconStereotype() throws Exception {
        assertEquals(Glyphicon.asterisk, util.getIcon(A.class, x -> x.foobar()));
    }

}
