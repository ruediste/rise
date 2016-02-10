package com.github.ruediste.rise.component.binding;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Path;

public class ValidationPathUtil {

    /**
     * Convert a javax.validation path to a beanutils property
     */
    public static String toPathString(Path path) {
        return StreamSupport.stream(path.spliterator(), false)
                .map(ValidationPathUtil::toPathString)
                .collect(Collectors.joining("."));
    }

    /**
     * Convert a single node of a javax.validation path to the corresponding
     * beanutils property path segment.
     */
    public static String toPathString(Path.Node node) {
        if (!node.isInIterable()) {
            return node.getName();
        } else {
            if (node.getIndex() != null) {
                return node.getName() + "[" + node.getIndex() + "]";
            }
            return node.getName() + "(" + node.getIndex() + ")";
        }
    }

}
