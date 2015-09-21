package com.github.ruediste.rise.core.argumentSerializer;

import java.util.Optional;

import com.github.ruediste.rise.util.Pair;
import com.google.common.base.Strings;

public class SerializerHelper {

    /**
     * Generate a String representing the value and an optional prefix. It can
     * later be parsed using {@link #parsePrefix(String)}
     * 
     * @param prefix
     *            prefix to prepend. May not be Optional.of("") or contain
     *            colons
     */
    public static String generatePrefix(Optional<String> prefix, String value) {
        if (Optional.of("").equals(prefix))
            throw new IllegalArgumentException(
                    "Prefix may not be the empty string");

        if (prefix.filter(s -> s.contains(":")).isPresent())
            throw new IllegalArgumentException(
                    "Prefix may not contain a colon (:)");

        if (value.contains(":"))
            return prefix.orElse("") + ":" + value;
        return prefix.map(s -> s + ":").orElse("") + value;
    }

    /**
     * Extract the prefix from a string generated with
     * {@link #generatePrefix(Optional, String)}
     */
    public static Pair<Optional<String>, String> parsePrefix(String value) {
        int idx = value.indexOf(':');
        if (idx < 0)
            return Pair.of(Optional.empty(), value);
        else
            return Pair.of(
                    Optional.ofNullable(
                            Strings.emptyToNull(value.substring(0, idx))),
                    value.substring(idx + 1));
    }
}
