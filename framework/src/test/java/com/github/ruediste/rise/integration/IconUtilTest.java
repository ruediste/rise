package com.github.ruediste.rise.integration;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Test;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;

public class IconUtilTest {

    @IconAnnotation
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private @interface TestIcon {
        Glyphicon value();
    }

    interface A {
        @TestIcon(Glyphicon.apple)
        void foo();

        void bar();
    }

    static class B implements A {

        @Override
        public void foo() {
        }

        @TestIcon(Glyphicon.alert)
        @Override
        public void bar() {

        }
    }

    IconUtil util = new IconUtil();

    @Test
    public void testGetIcon() throws Exception {
        assertEquals(Glyphicon.apple, util.getIcon(A.class, x -> x.foo()));
        assertEquals(Glyphicon.apple, util.getIcon(B.class, x -> x.foo()));
        assertEquals(Glyphicon.alert, util.getIcon(B.class, x -> x.bar()));
    }

}
