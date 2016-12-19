package com.github.ruediste.rise.es.api;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.es.api.EsNameHelper;

public class EsNameHelperTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testUpperCamelToLowerHyphen() throws Exception {
        check("", "");
        check("foo", "foo");
        check("foo", "Foo");
        check("a-b", "AB");
        check("foo-bar", "FooBar");
        check("the-f-o-o-bar", "TheFOOBar");
    }

    private void check(String expected, String input) {
        assertEquals(expected, EsNameHelper.upperCamelToLowerHyphen(input));
    }
}
