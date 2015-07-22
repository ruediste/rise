package com.github.ruediste.rise.core.argumentSerializer;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import com.github.ruediste.rise.util.Pair;

public class SerializerHelperTest {

    @Test
    public void simple() {
        check(Optional.empty(), "123", "123");
        check(Optional.of("456"), "123", "456:123");
        check(Optional.empty(), "def", "def");
        check(Optional.of("abc"), "def", "abc:def");
        check(Optional.empty(), "", "");
        check(Optional.of("abc"), "", "abc:");
    }

    @Test
    public void withEscapeCharInValue() {
        check(Optional.empty(), "1:23", ":1:23");
        check(Optional.of("456"), "123:", "456:123:");
        check(Optional.empty(), ":def", "::def");
        check(Optional.of("abc"), "de:f", "abc:de:f");
        check(Optional.empty(), ":", "::");
        check(Optional.of("abc"), ":", "abc::");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyStringPrefix() {
        SerializerHelper.generatePrefix(Optional.of(""), "123");

    }

    private void check(Optional<String> prefix, String value,
            String expectedPrefixed) {
        String prefixed = SerializerHelper.generatePrefix(prefix, value);
        Pair<Optional<String>, String> parsed = SerializerHelper
                .parsePrefix(prefixed);
        String message = "Difference detected for <" + prefix + ">:<" + value
                + ">. Prefixed String was <" + prefixed + ">\n";
        assertEquals(message, expectedPrefixed, prefixed);
        assertEquals(message, prefix, parsed.getA());
        assertEquals(message, value, parsed.getB());
    }
}
